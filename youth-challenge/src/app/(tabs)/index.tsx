import { useQueryClient } from '@tanstack/react-query';
import { LinearGradient } from 'expo-linear-gradient';
import { useRouter } from 'expo-router';
import * as WebBrowser from 'expo-web-browser';
import { useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Typography } from '@/constants/theme';
import { useSessionStore } from '@/features/auth/store/session-store';
import { DEVICES_QUERY_KEY } from '@/features/devices/api/use-devices';
import { useLinkToken } from '@/features/devices/api/use-link-token';
import { VITALS_QUERY_KEY } from '@/features/devices/api/use-vitals';
import { BiomarkerPill } from '@/features/home/components/biomarker-pill';
import { CarouselDots } from '@/features/home/components/carousel-dots';
import { ConnectDeviceCard } from '@/features/home/components/connect-device-card';
import { ConnectDeviceSheet, DeviceOption } from '@/features/home/components/connect-device-sheet';
import { DeviceActionsSheet } from '@/features/home/components/device-actions-sheet';
import { DemoConnectSheet } from '@/features/home/components/demo-connect-sheet';
import { HealthScoreGauge } from '@/features/home/components/health-score-gauge';
import { HomeHeader } from '@/features/home/components/home-header';
import { WhoopConnectPromptSheet } from '@/features/home/components/whoop-connect-prompt-sheet';

const c = Colors.light;

const DEMO_CAPABLE_SLUGS = ['oura', 'apple_health_kit'];

/** Warm aurora background, approximated with a vertical base gradient plus a
 * diagonal amber glow layered on top. */
const BASE_GRADIENT = ['#241C1A', '#3A2A20', '#5E3F28', '#7A5233', '#835836'] as const;

export default function HomeScreen() {
  const insets = useSafeAreaInsets();
  const router = useRouter();
  const user = useSessionStore((state) => state.user);
  const username = user?.username?.trim();
  const [deviceSheetOpen, setDeviceSheetOpen] = useState(false);
  const [actionDevice, setActionDevice] = useState<{
    option: DeviceOption;
    provider: string;
  } | null>(null);
  const [whoopPromptOpen, setWhoopPromptOpen] = useState(false);
  const [demoDevice, setDemoDevice] = useState<DeviceOption | null>(null);
  const linkTokenMutation = useLinkToken();
  const queryClient = useQueryClient();

  const handleSelectDevice = (device: DeviceOption, connectedProvider: string | null) => {
    setDeviceSheetOpen(false);
    if (connectedProvider) {
      setActionDevice({ option: device, provider: connectedProvider });
    } else if (device.name === 'Whoop') {
      setWhoopPromptOpen(true);
    } else if (DEMO_CAPABLE_SLUGS.includes(device.slugs[0])) {
      setDemoDevice(device);
    }
  };

  const handleConnectWhoop = () => {
    if (linkTokenMutation.isPending) return;
    linkTokenMutation.mutate('whoop', {
      onSuccess: async (result) => {
        if (result.type !== 'success') {
          const message =
            result.type === 'notProvisioned'
              ? 'Your account is still being set up. Please try again in a moment.'
              : result.type === 'unauthorized'
                ? 'Your session has expired. Please sign in again.'
                : result.message;
          Alert.alert('Could not connect', message);
          return;
        }
        try {
          await WebBrowser.openBrowserAsync(result.linkWebUrl);
        } catch {
          Alert.alert('Could not connect', 'Could not open the connection page. Please try again.');
          return;
        }
        queryClient.invalidateQueries({ queryKey: DEVICES_QUERY_KEY });
        queryClient.invalidateQueries({ queryKey: VITALS_QUERY_KEY });
        setWhoopPromptOpen(false);
      },
    });
  };

  return (
    <>
    <ScrollView
      style={styles.root}
      contentContainerStyle={styles.content}
      showsVerticalScrollIndicator={false}>
      <View style={styles.hero}>
        <LinearGradient colors={BASE_GRADIENT} style={StyleSheet.absoluteFill} />
        <LinearGradient
          colors={['rgba(255,136,17,0.22)', 'rgba(255,136,17,0)']}
          start={{ x: 0, y: 0.15 }}
          end={{ x: 0.9, y: 0.8 }}
          style={StyleSheet.absoluteFill}
        />

        <View style={[styles.heroContent, { paddingTop: insets.top + 12 }]}>
          <HomeHeader
            name={username || 'Add your name'}
            initial={(username?.[0] ?? 'S').toUpperCase()}
            onPressAvatar={() => router.push('/settings')}
          />

          <View style={styles.scoreBlock}>
            <Text style={styles.h1}>Health Score</Text>
            <Text style={styles.updating}>UPDATING...</Text>
            <HealthScoreGauge score={99} label="Optimal" />
            <BiomarkerPill count={4} total={10} />
            <CarouselDots count={2} active={0} />
          </View>

          <ConnectDeviceCard onConnect={() => setDeviceSheetOpen(true)} />
          <View style={styles.cardDots}>
            <CarouselDots count={4} active={0} />
          </View>
        </View>
      </View>

      <View style={styles.sheet}>
        <Text style={styles.sheetTitle}>Health areas to improve</Text>
      </View>
    </ScrollView>

    <ConnectDeviceSheet
      visible={deviceSheetOpen}
      onClose={() => setDeviceSheetOpen(false)}
      onSelect={handleSelectDevice}
    />

    <DeviceActionsSheet device={actionDevice} onClose={() => setActionDevice(null)} />

    <DemoConnectSheet device={demoDevice} onClose={() => setDemoDevice(null)} />

    <WhoopConnectPromptSheet
      visible={whoopPromptOpen}
      onCancel={() => setWhoopPromptOpen(false)}
      onContinue={handleConnectWhoop}
    />
    </>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: c.appBackground,
  },
  content: {
    paddingBottom: 40,
  },
  hero: {
    overflow: 'hidden',
  },
  heroContent: {
    paddingHorizontal: 24,
    paddingBottom: 48,
    gap: 24,
  },
  scoreBlock: {
    alignItems: 'center',
    gap: 20,
  },
  h1: {
    ...Typography.title40,
    fontSize: 32,
    lineHeight: 38,
    color: Primitives.white,
    marginTop: 8,
  },
  updating: {
    ...Typography.caps12,
    color: 'rgba(255,255,255,0.55)',
    letterSpacing: 2,
  },
  cardDots: {
    marginTop: 4,
  },
  sheet: {
    backgroundColor: c.appBackground,
    borderTopLeftRadius: 36,
    borderTopRightRadius: 36,
    marginTop: -28,
    paddingTop: 32,
    paddingHorizontal: 24,
    minHeight: 160,
  },
  sheetTitle: {
    ...Typography.title24,
    color: Primitives.black,
  },
});

import { LinearGradient } from 'expo-linear-gradient';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Typography } from '@/constants/theme';
import { useSessionStore } from '@/features/auth/store/session-store';
import { BiomarkerPill } from '@/features/home/components/biomarker-pill';
import { CarouselDots } from '@/features/home/components/carousel-dots';
import { ConnectDeviceCard } from '@/features/home/components/connect-device-card';
import { ConnectDeviceSheet } from '@/features/home/components/connect-device-sheet';
import { HealthScoreGauge } from '@/features/home/components/health-score-gauge';
import { HomeHeader } from '@/features/home/components/home-header';
import { WhoopConnectPromptSheet } from '@/features/home/components/whoop-connect-prompt-sheet';
import { WhoopConnectedSheet } from '@/features/home/components/whoop-connected-sheet';

const c = Colors.light;

/** Warm aurora background, approximated with a vertical base gradient plus a
 * diagonal amber glow layered on top. */
const BASE_GRADIENT = ['#241C1A', '#3A2A20', '#5E3F28', '#7A5233', '#835836'] as const;

export default function HomeScreen() {
  const insets = useSafeAreaInsets();
  const router = useRouter();
  const user = useSessionStore((state) => state.user);
  const username = user?.username?.trim();
  const [deviceSheetOpen, setDeviceSheetOpen] = useState(false);
  const [whoopPromptOpen, setWhoopPromptOpen] = useState(false);
  const [whoopSheetOpen, setWhoopSheetOpen] = useState(false);

  const handleSelectDevice = (device: string) => {
    setDeviceSheetOpen(false);
    if (device === 'Whoop') {
      setWhoopPromptOpen(true);
    }
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

    <WhoopConnectPromptSheet
      visible={whoopPromptOpen}
      onCancel={() => setWhoopPromptOpen(false)}
      onContinue={() => {
        setWhoopPromptOpen(false);
        setWhoopSheetOpen(true);
      }}
    />

    <WhoopConnectedSheet
      visible={whoopSheetOpen}
      onClose={() => setWhoopSheetOpen(false)}
      onNext={() => setWhoopSheetOpen(false)}
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

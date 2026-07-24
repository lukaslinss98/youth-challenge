import { useRouter } from 'expo-router';
import { useState } from 'react';
import { ImageBackground, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Typography } from '@/constants/theme';
import { useSessionStore } from '@/features/auth/store/session-store';
import { ActionCardCarousel } from '@/features/home/components/action-cards/action-card-carousel';
import { BiomarkerPill } from '@/features/home/components/biomarker-pill';
import { CarouselDots } from '@/features/home/components/carousel-dots';
import { ConnectFlowSheet } from '@/features/home/components/connect-flow/connect-flow-sheet';
import { DeviceActionsSheet } from '@/features/home/components/device-actions-sheet';
import { DeviceOption } from '@/features/home/components/device-options';
import { HealthAreaRow } from '@/features/home/components/health-areas/health-area-row';
import { HealthAreas } from '@/features/home/components/health-areas/health-areas';
import { HealthScoreGauge } from '@/features/home/components/health-score-gauge';
import { HomeFooter } from '@/features/home/components/home-footer';
import { HomeHeader } from '@/features/home/components/home-header';

const c = Colors.light;

export default function HomeScreen() {
  const insets = useSafeAreaInsets();
  const router = useRouter();
  const user = useSessionStore((state) => state.user);
  const username = user?.username?.trim();
  const [connectFlowOpen, setConnectFlowOpen] = useState(false);
  const [actionDevice, setActionDevice] = useState<{
    option: DeviceOption;
    provider: string;
  } | null>(null);

  return (
    <>
    <ScrollView
      style={styles.root}
      contentContainerStyle={styles.content}
      showsVerticalScrollIndicator={false}>
      <ImageBackground
        source={require('@/assets/images/home-aurora.png')}
        style={styles.hero}
        imageStyle={styles.heroImage}>
        <View style={[styles.heroContent, { paddingTop: insets.top + 12 }]}>
          <HomeHeader
            name={username || 'Add your name'}
            initial={(username?.[0] ?? 'S').toUpperCase()}
            onPressAvatar={() => router.push('/settings')}
          />

          <View style={styles.scoreBlock}>
            <Text style={styles.h1}>Health Score</Text>
            <Text style={styles.updated}>UPDATED TODAY</Text>
            <HealthScoreGauge score={99} label="Optimal" />
            <BiomarkerPill count={4} total={10} />
            <CarouselDots count={2} active={0} />
          </View>
        </View>

        <View style={styles.carouselBlock}>
          <ActionCardCarousel onConnect={() => setConnectFlowOpen(true)} />
        </View>
      </ImageBackground>

      <View style={styles.sheet}>
        <Text style={styles.sheetTitle}>Health areas to improve</Text>
        <HealthAreas />

        <Text style={styles.sheetTitle}>Health areas to unlock</Text>
        <HealthAreaRow
          name="Face skin"
          subtitle="Run checkup to see results"
          actionLabel="CHECK"
        />

        <HomeFooter />
      </View>
    </ScrollView>

    <ConnectFlowSheet
      visible={connectFlowOpen}
      onClose={() => setConnectFlowOpen(false)}
      onSelectConnected={(option, provider) => setActionDevice({ option, provider })}
    />

    <DeviceActionsSheet device={actionDevice} onClose={() => setActionDevice(null)} />
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
    backgroundColor: '#241C1A',
  },
  heroImage: {
    resizeMode: 'cover',
  },
  heroContent: {
    paddingHorizontal: 24,
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
  updated: {
    ...Typography.caps12,
    color: 'rgba(255,255,255,0.55)',
    letterSpacing: 2,
  },
  carouselBlock: {
    paddingTop: 24,
    paddingBottom: 48,
  },
  sheet: {
    backgroundColor: c.appBackground,
    borderTopLeftRadius: 36,
    borderTopRightRadius: 36,
    marginTop: -28,
    paddingTop: 32,
    paddingHorizontal: 24,
    paddingBottom: 8,
    gap: 20,
  },
  sheetTitle: {
    ...Typography.title24,
    color: Primitives.black,
    marginTop: 8,
  },
});

import { useState } from 'react';
import { ScrollView, StyleSheet, Text, View, useWindowDimensions } from 'react-native';

import { Colors, Primitives, Typography } from '@/constants/theme';
import {
  ActionCard,
  ActionCardButton,
  ActionCardIcon,
} from '@/features/home/components/action-cards/action-card';
import { CarouselDots } from '@/features/home/components/carousel-dots';

const c = Colors.light;

const CARD_GAP = 10;
const ICON_TEXTURE = require('@/assets/images/action-card-icon.png');

export function ActionCardCarousel({ onConnect }: { onConnect: () => void }) {
  const { width } = useWindowDimensions();
  const cardWidth = width - 43;
  const [active, setActive] = useState(0);

  const cards = [
    <NextCheckupCard key="checkup" />,
    <ConnectDevicesCard key="connect" onConnect={onConnect} />,
    <AnalysisCompletedCard key="analysis" />,
  ];

  return (
    <View style={styles.root}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        snapToInterval={cardWidth + CARD_GAP}
        decelerationRate="fast"
        contentContainerStyle={styles.content}
        onMomentumScrollEnd={(event) =>
          setActive(Math.round(event.nativeEvent.contentOffset.x / (cardWidth + CARD_GAP)))
        }>
        {cards.map((card, index) => (
          <View
            key={index}
            style={{ width: cardWidth, marginRight: index === cards.length - 1 ? 0 : CARD_GAP }}>
            {card}
          </View>
        ))}
      </ScrollView>
      <CarouselDots count={cards.length} active={active} />
    </View>
  );
}

function NextCheckupCard() {
  return (
    <ActionCard>
      <View style={styles.headRow}>
        <ActionCardIcon source={ICON_TEXTURE} tint="#F37953" />
        <View style={styles.headText}>
          <Text style={styles.overline}>NEXT CHECK UP</Text>
          <Text style={styles.title}>Cardiovascular</Text>
          <Text style={styles.subtitle}>Face video • 1 min</Text>
        </View>
      </View>
      <ActionCardButton label="CHECK NOW" />
    </ActionCard>
  );
}

function ConnectDevicesCard({ onConnect }: { onConnect: () => void }) {
  return (
    <ActionCard>
      <View style={styles.headRow}>
        <ActionCardIcon source={ICON_TEXTURE} tint="#F37953" />
        <View style={styles.headText}>
          <Text style={styles.title}>Connect your devices</Text>
          <Text style={styles.subtitle}>Unlock more insights by connecting your wearable devices</Text>
        </View>
      </View>
      <ActionCardButton label="CONNECT A DEVICE" onPress={onConnect} />
    </ActionCard>
  );
}

function AnalysisCompletedCard() {
  return (
    <ActionCard variant="light">
      <View style={styles.headText}>
        <Text style={styles.overlineLight}>ANALYSIS COMPLETED</Text>
        <Text style={styles.titleLight}>Complete Blood Count (CBC)</Text>
        <View style={styles.metaRow}>
          <Text style={styles.metaText}>02 Jan, 2025</Text>
          <Text style={styles.metaText}>·</Text>
          <Text style={styles.metaWarning}>OUT OF RANGE</Text>
        </View>
      </View>
      <ActionCardButton label="VIEW RESULTS" variant="light" />
    </ActionCard>
  );
}

const styles = StyleSheet.create({
  root: { gap: 16 },
  content: {
    paddingHorizontal: 24,
  },
  headRow: {
    flexDirection: 'row',
    gap: 12,
  },
  headText: {
    flex: 1,
    gap: 4,
  },
  overline: {
    ...Typography.caps10,
    color: 'rgba(255,255,255,0.5)',
    letterSpacing: 1,
  },
  overlineLight: {
    ...Typography.caps10,
    color: c.textSecondary,
    letterSpacing: 1,
  },
  title: {
    ...Typography.title16,
    fontSize: 18,
    lineHeight: 24,
    color: Primitives.white,
  },
  titleLight: {
    ...Typography.title16,
    fontSize: 18,
    lineHeight: 24,
    color: c.text,
  },
  subtitle: {
    ...Typography.body14,
    fontSize: 12,
    lineHeight: 16,
    color: 'rgba(255,255,255,0.5)',
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  metaText: {
    ...Typography.body14,
    fontSize: 12,
    color: c.textSecondary,
  },
  metaWarning: {
    ...Typography.caps10,
    color: c.warning,
    letterSpacing: 0.5,
  },
});

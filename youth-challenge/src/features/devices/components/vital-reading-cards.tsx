import { StyleSheet, Text, View } from 'react-native';

import { Colors, Radius, Typography } from '@/constants/theme';
import type { VitalReading } from '@/features/devices/types';

const c = Colors.light;

const METRIC_LABELS: Record<string, string> = {
  HEART_RATE: 'Heart Rate',
  HEART_RATE_VARIABILITY: 'Heart Rate Variability',
  RESPIRATORY_RATE: 'Breathing Rate',
  BLOOD_OXYGEN: 'Oxygen Saturation',
  BLOOD_PRESSURE: 'Blood Pressure',
};

export function metricLabel(metric: string) {
  if (metric.startsWith('BLOOD_PRESSURE')) return METRIC_LABELS.BLOOD_PRESSURE;
  return METRIC_LABELS[metric] ?? metric;
}

type Card = { key: string; label: string; value: string; unit: string; measuredAt: string };

function formatValue(value: number) {
  return Number.isInteger(value) ? String(value) : value.toFixed(1);
}

function formatWhen(iso: string) {
  return new Date(iso).toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function toCards(readings: VitalReading[]): Card[] {
  const byMetric = new Map(readings.map((r) => [r.metric, r]));
  const cards: Card[] = [];

  const systolic = byMetric.get('BLOOD_PRESSURE_SYSTOLIC');
  const diastolic = byMetric.get('BLOOD_PRESSURE_DIASTOLIC');
  if (systolic && diastolic) {
    cards.push({
      key: 'BLOOD_PRESSURE',
      label: METRIC_LABELS.BLOOD_PRESSURE,
      value: `${formatValue(systolic.value)}/${formatValue(diastolic.value)}`,
      unit: systolic.unit ?? '',
      measuredAt: systolic.measuredAt,
    });
  }

  for (const reading of readings) {
    if (reading.metric.startsWith('BLOOD_PRESSURE')) continue;
    cards.push({
      key: reading.metric,
      label: METRIC_LABELS[reading.metric] ?? reading.metric,
      value: formatValue(reading.value),
      unit: reading.unit ?? '',
      measuredAt: reading.measuredAt,
    });
  }

  return cards;
}

export function VitalReadingCards({ readings }: { readings: VitalReading[] }) {
  const cards = toCards(readings);

  return (
    <View style={styles.grid}>
      {cards.map((card) => (
        <View key={card.key} style={styles.card}>
          <Text style={styles.label}>{card.label}</Text>
          <View style={styles.valueRow}>
            <Text style={styles.value}>{card.value}</Text>
            {card.unit ? <Text style={styles.unit}>{card.unit}</Text> : null}
          </View>
          <Text style={styles.when}>{formatWhen(card.measuredAt)}</Text>
        </View>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  card: {
    flexGrow: 1,
    flexBasis: '46%',
    gap: 6,
    padding: 16,
    borderRadius: Radius.md,
    backgroundColor: '#F1F1F1',
  },
  label: {
    ...Typography.caps10,
    color: c.textSecondary,
  },
  valueRow: {
    flexDirection: 'row',
    alignItems: 'baseline',
    gap: 4,
  },
  value: {
    ...Typography.title24,
    color: c.text,
  },
  unit: {
    ...Typography.body14,
    color: c.textSecondary,
  },
  when: {
    ...Typography.body14,
    color: c.textSecondary,
  },
});

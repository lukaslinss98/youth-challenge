import { StyleSheet, View } from 'react-native';

import { useVitals } from '@/features/devices/api/use-vitals';
import { HealthAreaRow } from '@/features/home/components/health-areas/health-area-row';

const AREAS = [
  {
    name: 'Cardiovascular',
    metrics: [
      'HEART_RATE',
      'HEART_RATE_VARIABILITY',
      'BLOOD_PRESSURE_SYSTOLIC',
      'BLOOD_PRESSURE_DIASTOLIC',
    ],
  },
  { name: 'Respiratory', metrics: ['RESPIRATORY_RATE', 'BLOOD_OXYGEN'] },
  { name: 'Metabolic', metrics: [] as string[] },
  { name: 'Blood', metrics: [] as string[] },
];

export function HealthAreas() {
  const { data: readings } = useVitals();
  const syncedMetrics = new Set((readings ?? []).map((reading) => reading.metric));

  return (
    <View style={styles.stack}>
      {AREAS.map((area) => {
        const synced = area.metrics.filter((metric) => syncedMetrics.has(metric)).length;
        const subtitle =
          synced > 0
            ? `${synced} /${area.metrics.length} biomarkers synced`
            : '2 /4 biomarkers to improve';
        return <HealthAreaRow key={area.name} name={area.name} subtitle={subtitle} status="Average" />;
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  stack: { gap: 16 },
});

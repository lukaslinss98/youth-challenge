import { LinearGradient } from 'expo-linear-gradient';
import { StyleSheet, Text, View } from 'react-native';

import { Colors, Primitives, Typography } from '@/constants/theme';

const c = Colors.light;

const CHIP_LAYOUT = [
  { width: '62%', opacity: 0.45, scale: 0.85 },
  { width: '78%', opacity: 0.7, scale: 0.92 },
  { width: '92%', opacity: 1, scale: 1 },
  { width: '78%', opacity: 0.7, scale: 0.92 },
  { width: '62%', opacity: 0.45, scale: 0.85 },
] as const;

export function StageSyncInfo({ biomarkers }: { biomarkers: string[] }) {
  const labels = CHIP_LAYOUT.map((_, index) => biomarkers[index % Math.max(biomarkers.length, 1)]);

  return (
    <View style={styles.hero}>
      <LinearGradient
        colors={['#A3A3A3', '#D7D7D7', '#F6F6F6']}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={StyleSheet.absoluteFill}
      />
      <LinearGradient
        colors={['rgba(224,255,243,0.2)', 'rgba(113,124,120,0.2)']}
        style={StyleSheet.absoluteFill}
      />
      <View style={styles.stack}>
        {CHIP_LAYOUT.map((layout, index) => {
          const focused = layout.scale === 1;
          return (
            <View
              key={index}
              style={[
                styles.chip,
                { width: layout.width, opacity: layout.opacity, transform: [{ scale: layout.scale }] },
                focused && styles.chipFocused,
              ]}>
              <Text style={styles.chipName} numberOfLines={1}>
                {labels[index] ?? 'Biomarker'}
              </Text>
              <View style={styles.pill}>
                <View style={styles.pillBar} />
                <Text style={styles.pillText}>Synced</Text>
              </View>
            </View>
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  hero: {
    flex: 1,
    borderRadius: 24,
    overflow: 'hidden',
    justifyContent: 'center',
  },
  stack: {
    alignItems: 'center',
    gap: 10,
  },
  chip: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    backgroundColor: c.surface,
    borderWidth: 1.5,
    borderColor: Primitives.white,
    borderRadius: 14,
    paddingVertical: 14,
    paddingHorizontal: 14,
  },
  chipFocused: {
    shadowColor: '#000',
    shadowOpacity: 0.15,
    shadowRadius: 21,
    shadowOffset: { width: 0, height: 0 },
    elevation: 8,
  },
  chipName: {
    ...Typography.title16,
    color: c.text,
    flex: 1,
  },
  pill: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
  },
  pillBar: {
    width: 4,
    height: 10,
    borderRadius: 7,
    backgroundColor: c.textSuccess,
  },
  pillText: {
    ...Typography.body14,
    color: c.text,
  },
});

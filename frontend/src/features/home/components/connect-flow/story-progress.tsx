import { StyleSheet, View } from 'react-native';

import { Primitives } from '@/constants/theme';

export function StoryProgress({ steps, active }: { steps: number; active: number }) {
  return (
    <View style={styles.row}>
      {Array.from({ length: steps }, (_, index) => (
        <View key={index} style={[styles.segment, index === active && styles.segmentActive]} />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    gap: 8,
    paddingVertical: 16,
    paddingHorizontal: 4,
  },
  segment: {
    flex: 1,
    height: 3,
    borderRadius: 60,
    backgroundColor: Primitives.white,
    opacity: 0.21,
  },
  segmentActive: {
    opacity: 1,
  },
});

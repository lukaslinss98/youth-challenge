import { StyleSheet, View } from 'react-native';

/** Row of pagination dots; the active one renders as a wider bar. */
export function CarouselDots({ count, active = 0 }: { count: number; active?: number }) {
  return (
    <View style={styles.row}>
      {Array.from({ length: count }).map((_, i) => (
        <View key={i} style={[styles.dot, i === active && styles.active]} />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 6,
  },
  dot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: 'rgba(255,255,255,0.3)',
  },
  active: {
    width: 18,
    backgroundColor: 'rgba(255,255,255,0.9)',
  },
});

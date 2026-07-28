import { StyleSheet, Text, View } from 'react-native';

import { Primitives, Radius, Typography } from '@/constants/theme';

/** Rounded pill summarising how many biomarkers need attention. */
export function BiomarkerPill({ count = 4, total = 10 }: { count?: number; total?: number }) {
  return (
    <View style={styles.pill}>
      <Text style={styles.strong}>
        {count}/{total}
      </Text>
      <Text style={styles.label}>Biomarkers to improve</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  pill: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 20,
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: Radius.pill,
    backgroundColor: 'rgba(255,255,255,0.10)',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.12)',
  },
  strong: {
    ...Typography.title16,
    color: Primitives.white,
  },
  label: {
    ...Typography.title16,
    color: 'rgba(255,255,255,0.9)',
  },
});

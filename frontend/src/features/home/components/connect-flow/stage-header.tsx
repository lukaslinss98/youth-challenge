import { StyleSheet, Text, View } from 'react-native';

import { Colors, Typography } from '@/constants/theme';

const c = Colors.light;

export function StageHeader({
  label,
  labelColor = c.textSuccess,
  step,
  steps,
  title,
}: {
  label: string;
  labelColor?: string;
  step: number;
  steps: number;
  title: string;
}) {
  return (
    <View style={styles.root}>
      <View style={styles.metaRow}>
        <Text style={[styles.label, { color: labelColor }]}>{label}</Text>
        <Text style={styles.step}>
          {step}/{steps}
        </Text>
      </View>
      <Text style={styles.title}>{title}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { gap: 10 },
  metaRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  label: {
    ...Typography.caps12,
    letterSpacing: 1,
  },
  step: {
    ...Typography.caps12,
    color: c.text,
  },
  title: {
    ...Typography.title24,
    color: c.text,
    paddingRight: 44,
  },
});

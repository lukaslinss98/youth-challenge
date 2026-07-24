import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Colors, Radius, Typography } from '@/constants/theme';
import { ChevronRight } from '@/features/home/components/device-icons';

const c = Colors.light;

type DeviceRowProps = {
  name: string;
  icon: (p: { color?: string; size?: number }) => React.ReactElement;
  status?: string;
  onPress?: () => void;
};

export function DeviceRow({ name, icon: Icon, status = 'NOT CONNECTED', onPress }: DeviceRowProps) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [styles.row, pressed && styles.pressed]}>
      <Icon color={c.text} size={22} />
      <View style={styles.text}>
        <Text style={styles.name}>{name}</Text>
        <Text style={[styles.status, status === 'CONNECTED' && styles.statusConnected]}>{status}</Text>
      </View>
      <ChevronRight color={c.textSecondary} size={20} />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderRadius: Radius.md,
    backgroundColor: '#F1F1F1',
  },
  pressed: { opacity: 0.6 },
  text: { flex: 1, gap: 3 },
  name: {
    ...Typography.title16,
    color: c.text,
  },
  status: {
    ...Typography.caps10,
    color: c.textSecondary,
  },
  statusConnected: {
    color: c.textSuccess,
  },
});

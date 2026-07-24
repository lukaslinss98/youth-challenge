import { LinearGradient } from 'expo-linear-gradient';
import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Colors, Primitives, Typography } from '@/constants/theme';
import { ChevronRight } from '@/features/home/components/device-icons';

const c = Colors.light;

const ICON_GRADIENT = ['#AF3F4D', '#CC723D', '#D8C1B1', '#F1F5E7'] as const;

export function HealthAreaRow({
  name,
  subtitle,
  status,
  statusColor = c.warning,
  actionLabel,
  onAction,
}: {
  name: string;
  subtitle: string;
  status?: string;
  statusColor?: string;
  actionLabel?: string;
  onAction?: () => void;
}) {
  return (
    <View style={styles.row}>
      <LinearGradient colors={ICON_GRADIENT} style={styles.icon} />
      <View style={styles.text}>
        <Text style={styles.name}>{name}</Text>
        <Text style={styles.subtitle}>{subtitle}</Text>
      </View>
      {status && (
        <View style={styles.statusGroup}>
          <View style={[styles.statusBar, { backgroundColor: statusColor }]} />
          <Text style={styles.statusText}>{status}</Text>
          <View style={styles.chevron}>
            <ChevronRight color={c.text} size={14} />
          </View>
        </View>
      )}
      {actionLabel && (
        <Pressable
          onPress={onAction}
          style={({ pressed }) => [styles.actionButton, pressed && styles.pressed]}>
          <Text style={styles.actionText}>{actionLabel}</Text>
        </Pressable>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
    backgroundColor: c.surface,
    borderWidth: 1.5,
    borderColor: Primitives.white,
    borderRadius: 16,
    paddingVertical: 14,
    paddingLeft: 16,
    paddingRight: 18,
  },
  icon: {
    width: 48,
    height: 48,
    borderRadius: 11,
  },
  text: {
    flex: 1,
    gap: 5,
  },
  name: {
    ...Typography.title16,
    fontSize: 18,
    lineHeight: 24,
    color: c.text,
  },
  subtitle: {
    ...Typography.body14,
    fontSize: 12,
    lineHeight: 16,
    color: c.textSecondary,
  },
  statusGroup: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  statusBar: {
    width: 5,
    height: 12,
    borderRadius: 9,
  },
  statusText: {
    ...Typography.body14,
    color: c.text,
  },
  chevron: {
    marginLeft: 6,
    transform: [{ rotate: '90deg' }],
  },
  actionButton: {
    backgroundColor: c.buttonPrimary,
    borderRadius: 10,
    paddingVertical: 10,
    paddingHorizontal: 16,
  },
  pressed: { opacity: 0.8 },
  actionText: {
    ...Typography.caps12,
    color: Primitives.white,
  },
});

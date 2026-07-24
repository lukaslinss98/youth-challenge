import { Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Typography } from '@/constants/theme';
import { useDevices } from '@/features/devices/api/use-devices';
import { AppleIcon, GarminIcon, OuraIcon, WhoopIcon } from '@/features/home/components/device-icons';
import { DeviceRow } from '@/features/home/components/device-row';

const c = Colors.light;

const DEVICES = [
  { name: 'Apple Watch', icon: AppleIcon, slugs: ['apple_health_kit'] },
  { name: 'Oura', icon: OuraIcon, slugs: ['oura'] },
  { name: 'Garmin', icon: GarminIcon, slugs: ['garmin'] },
  { name: 'Whoop', icon: WhoopIcon, slugs: ['whoop', 'whoop_v2'] },
] as const;

export type DeviceOption = (typeof DEVICES)[number];

function statusLabel(status: string) {
  if (status === 'PENDING') return 'CONNECTING…';
  if (status === 'ERROR') return 'CONNECTION FAILED';
  return status === 'CONNECTED' ? 'CONNECTED' : status.replace(/_/g, ' ');
}

/** Bottom sheet shown when the user chooses to connect a device. Slides up over
 * a dimmed backdrop; tapping the backdrop or a device closes it. */
export function ConnectDeviceSheet({
  visible,
  onClose,
  onSelect,
}: {
  visible: boolean;
  onClose: () => void;
  onSelect?: (device: DeviceOption, connectedProvider: string | null) => void;
}) {
  const insets = useSafeAreaInsets();
  const { data: devices } = useDevices();
  const statusBySlug = new Map((devices ?? []).map((d) => [d.provider, d.status]));

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose} />
      <View style={styles.container} pointerEvents="box-none">
        <View style={[styles.sheet, { paddingBottom: Math.max(insets.bottom, 20) }]}>
          <View style={styles.handle} />
          <Text style={styles.title}>Select a device</Text>

          <View style={styles.list}>
            {DEVICES.map((d) => {
              const slug = d.slugs.find((s) => statusBySlug.get(s));
              const status = slug ? statusBySlug.get(slug) : undefined;
              return (
                <DeviceRow
                  key={d.name}
                  name={d.name}
                  icon={d.icon}
                  status={status ? statusLabel(status) : 'NOT CONNECTED'}
                  onPress={() => onSelect?.(d, status === 'CONNECTED' && slug ? slug : null)}
                />
              );
            })}
          </View>

          <View style={styles.divider} />
          <View style={styles.disclaimer}>
            <Text style={styles.disclaimerLabel}>DISCLAIMER</Text>
            <Text style={styles.disclaimerBody}>
              Your historical data will be collected starting with next month&apos;s app update.
            </Text>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  container: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  sheet: {
    backgroundColor: c.surface,
    borderTopLeftRadius: 28,
    borderTopRightRadius: 28,
    paddingHorizontal: 20,
    paddingTop: 12,
    gap: 20,
  },
  handle: {
    alignSelf: 'center',
    width: 40,
    height: 5,
    borderRadius: 3,
    backgroundColor: c.surfaceGrey,
  },
  title: {
    ...Typography.title24,
    color: c.text,
  },
  list: { gap: 8 },
  divider: {
    height: 1,
    backgroundColor: c.divider,
  },
  disclaimer: { gap: 6 },
  disclaimerLabel: {
    ...Typography.caps10,
    color: c.textSecondary,
  },
  disclaimerBody: {
    ...Typography.body14,
    color: c.text,
  },
});

import { Image, StyleSheet, Text, View } from 'react-native';

import { Colors, Radius, Typography } from '@/constants/theme';
import { DeviceRow } from '@/features/home/components/device-row';
import { DEVICES, DeviceOption, statusLabel } from '@/features/home/components/device-options';

const c = Colors.light;

export function DevicePicker({
  statusBySlug,
  onSelect,
}: {
  statusBySlug: Map<string, string>;
  onSelect: (device: DeviceOption, connectedProvider: string | null) => void;
}) {
  return (
    <View style={styles.pickerRoot}>
      <View style={styles.list}>
        {DEVICES.map((device) => {
          const slug = device.slugs.find((s) => statusBySlug.get(s));
          const status = slug ? statusBySlug.get(slug) : undefined;
          return (
            <DeviceRow
              key={device.name}
              name={device.name}
              icon={device.icon}
              status={status ? statusLabel(status) : 'NOT CONNECTED'}
              style={styles.row}
              onPress={() => onSelect(device, status === 'CONNECTED' && slug ? slug : null)}
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
  );
}

export function ConnectPrompt({
  device,
  waiting,
  error,
}: {
  device: DeviceOption;
  waiting: boolean;
  error: string | null;
}) {
  return (
    <View style={styles.promptRoot}>
      <View style={styles.imageCard}>
        <Image source={device.image} style={styles.image} resizeMode="cover" />
      </View>
      <Text style={styles.promptBody}>
        {waiting
          ? `Finish the connection in your browser. This screen updates automatically once your ${device.name} is linked.`
          : "Select your device, log in securely, and confirm what data to share. You'll return here once setup is complete."}
      </Text>
      {error && <Text style={styles.error}>{error}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  pickerRoot: {
    flex: 1,
    gap: 20,
  },
  list: { gap: 8 },
  row: { backgroundColor: c.surface },
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
  promptRoot: {
    flex: 1,
    gap: 16,
  },
  imageCard: {
    flex: 1,
    borderRadius: Radius.md,
    overflow: 'hidden',
    backgroundColor: '#E3EAE5',
  },
  image: {
    width: '100%',
    height: '100%',
  },
  promptBody: {
    ...Typography.body14,
    color: c.textSecondary,
  },
  error: {
    ...Typography.body14,
    color: c.danger,
  },
});

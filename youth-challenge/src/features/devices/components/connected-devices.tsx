import { StyleSheet, Text, View } from 'react-native';

import { Colors, Typography } from '@/constants/theme';
import { useDevices } from '@/features/devices/api/use-devices';
import { AppleIcon, GarminIcon, OuraIcon, WhoopIcon } from '@/features/home/components/device-icons';
import { DeviceRow } from '@/features/home/components/device-row';

const c = Colors.light;

type IconComponent = (props: { color?: string; size?: number }) => React.ReactElement;

const PROVIDERS: Record<string, { label: string; icon: IconComponent }> = {
  whoop: { label: 'Whoop', icon: WhoopIcon },
  oura: { label: 'Oura', icon: OuraIcon },
  apple_health_kit: { label: 'Apple Watch', icon: AppleIcon },
  garmin: { label: 'Garmin', icon: GarminIcon },
};

function statusLabel(status: string) {
  return status === 'CONNECTED' ? 'CONNECTED' : status.replace(/_/g, ' ');
}

export function ConnectedDevices() {
  const { data: devices } = useDevices();

  if (!devices || devices.length === 0) {
    return null;
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Your devices</Text>
      <View style={styles.list}>
        {devices.map((device) => {
          const provider = PROVIDERS[device.provider];
          return (
            <DeviceRow
              key={device.provider}
              name={provider?.label ?? device.provider}
              icon={provider?.icon ?? OuraIcon}
              status={statusLabel(device.status)}
            />
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { gap: 16, marginTop: 24 },
  title: {
    ...Typography.title24,
    color: c.text,
  },
  list: { gap: 8 },
});

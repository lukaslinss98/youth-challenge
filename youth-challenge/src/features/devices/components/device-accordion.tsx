import { useState } from 'react';
import { LayoutAnimation, StyleSheet, Text, View } from 'react-native';

import { Colors, Typography } from '@/constants/theme';
import { useDevices } from '@/features/devices/api/use-devices';
import { useVitals } from '@/features/devices/api/use-vitals';
import { VitalReadingCards } from '@/features/devices/components/vital-reading-cards';
import type { Device, VitalReading } from '@/features/devices/types';
import { AppleIcon, GarminIcon, OuraIcon, WhoopIcon } from '@/features/home/components/device-icons';
import { DeviceRow } from '@/features/home/components/device-row';

const c = Colors.light;

type IconComponent = (props: { color?: string; size?: number }) => React.ReactElement;

const PROVIDERS: Record<string, { label: string; icon: IconComponent }> = {
  whoop: { label: 'Whoop', icon: WhoopIcon },
  whoop_v2: { label: 'Whoop', icon: WhoopIcon },
  oura: { label: 'Oura', icon: OuraIcon },
  apple_health_kit: { label: 'Apple Watch', icon: AppleIcon },
  garmin: { label: 'Garmin', icon: GarminIcon },
};

function statusLabel(status: string) {
  if (status === 'PENDING') return 'CONNECTING…';
  if (status === 'ERROR') return 'CONNECTION FAILED';
  return status === 'CONNECTED' ? 'CONNECTED' : status.replace(/_/g, ' ');
}

export function DeviceAccordion() {
  const { data: devices } = useDevices();
  const { data: readings } = useVitals();

  if (!devices || devices.length === 0) {
    return null;
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Your devices</Text>
      <View style={styles.list}>
        {devices.map((device, index) => (
          <DeviceAccordionItem
            key={device.provider}
            device={device}
            readings={(readings ?? []).filter((r) => r.provider === device.provider)}
            defaultExpanded={index === 0}
          />
        ))}
      </View>
    </View>
  );
}

function DeviceAccordionItem({
  device,
  readings,
  defaultExpanded,
}: {
  device: Device;
  readings: VitalReading[];
  defaultExpanded: boolean;
}) {
  const [expanded, setExpanded] = useState(defaultExpanded);
  const provider = PROVIDERS[device.provider];

  const toggle = () => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setExpanded((prev) => !prev);
  };

  return (
    <View>
      <DeviceRow
        name={provider?.label ?? device.provider}
        icon={provider?.icon ?? OuraIcon}
        status={statusLabel(device.status)}
        onPress={toggle}
        expanded={expanded}
      />
      {expanded ? (
        <View style={styles.body}>
          {readings.length > 0 ? (
            <VitalReadingCards readings={readings} />
          ) : (
            <Text style={styles.empty}>No readings yet — pull down to sync.</Text>
          )}
        </View>
      ) : null}
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
  body: { marginTop: 12 },
  empty: {
    ...Typography.body14,
    color: c.textSecondary,
  },
});

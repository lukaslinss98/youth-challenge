import { RefreshControl, ScrollView, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Typography } from '@/constants/theme';
import { useDevices } from '@/features/devices/api/use-devices';
import { useSyncVitals } from '@/features/devices/api/use-sync-vitals';
import { useVitals } from '@/features/devices/api/use-vitals';
import { DeviceAccordion } from '@/features/devices/components/device-accordion';

const c = Colors.light;

export default function DataScreen() {
  const insets = useSafeAreaInsets();
  const { data: devices } = useDevices();
  const { data: readings } = useVitals();
  const syncVitals = useSyncVitals();

  const hasDevices = !!devices && devices.length > 0;
  const hasReadings = !!readings && readings.length > 0;

  return (
    <View style={styles.root}>
      <ScrollView
        contentContainerStyle={[
          styles.content,
          { paddingTop: insets.top + 12, paddingBottom: insets.bottom + 32 },
        ]}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={syncVitals.isPending}
            onRefresh={() => syncVitals.mutate(undefined)}
            tintColor={Primitives.orange}
            colors={[Primitives.orange]}
          />
        }>
        <Text style={styles.header}>DATA</Text>

        {!hasDevices && !hasReadings ? (
          <View style={styles.empty}>
            <Text style={styles.emptyTitle}>No wearable data yet</Text>
            <Text style={styles.emptyBody}>
              Connect a device from the Home tab to start seeing your readings here.
            </Text>
          </View>
        ) : (
          <DeviceAccordion />
        )}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: c.appBackground,
  },
  content: {
    paddingHorizontal: 16,
  },
  header: {
    ...Typography.caps13,
    color: c.text,
    textAlign: 'center',
    textTransform: 'uppercase',
    paddingVertical: 12,
  },
  empty: {
    marginTop: 48,
    gap: 8,
    alignItems: 'center',
  },
  emptyTitle: {
    ...Typography.title18,
    color: c.text,
  },
  emptyBody: {
    ...Typography.body14,
    color: c.textSecondary,
    textAlign: 'center',
  },
});

import { ActivityIndicator, Alert, Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Radius, Typography } from '@/constants/theme';
import { useDisconnectDevice } from '@/features/devices/api/use-disconnect-device';
import { useSyncVitals } from '@/features/devices/api/use-sync-vitals';
import { DeviceOption } from '@/features/home/components/connect-device-sheet';

const c = Colors.light;

/** Bottom sheet with actions for an already-connected device. */
export function DeviceActionsSheet({
  device,
  onClose,
}: {
  device: { option: DeviceOption; provider: string } | null;
  onClose: () => void;
}) {
  const insets = useSafeAreaInsets();
  const syncVitals = useSyncVitals();
  const disconnectDevice = useDisconnectDevice();
  const actionPending = syncVitals.isPending || disconnectDevice.isPending;

  const handleResync = () => {
    if (!device || actionPending) return;
    syncVitals.mutate(device.provider, {
      onSuccess: ({ ingested }) => {
        onClose();
        Alert.alert(
          'Sync complete',
          ingested === 1 ? 'Synced 1 new reading.' : `Synced ${ingested} new readings.`,
        );
      },
      onError: () => {
        Alert.alert('Sync failed', 'Could not sync your data. Please try again.');
      },
    });
  };

  const handleDisconnect = () => {
    if (!device || actionPending) return;
    Alert.alert(
      'Disconnect device?',
      `${device.option.name} will stop syncing new data. Your existing data stays available.`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Disconnect',
          style: 'destructive',
          onPress: () =>
            disconnectDevice.mutate(device.provider, {
              onSuccess: onClose,
              onError: () => {
                Alert.alert('Disconnect failed', 'Could not disconnect the device. Please try again.');
              },
            }),
        },
      ],
    );
  };

  if (!device) return null;
  const Icon = device.option.icon;

  return (
    <Modal visible transparent animationType="slide" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose} />
      <View style={styles.container} pointerEvents="box-none">
        <View style={[styles.sheet, { paddingBottom: Math.max(insets.bottom, 20) }]}>
          <View style={styles.handle} />

          <View style={styles.header}>
            <Icon color={c.text} size={28} />
            <View style={styles.headerText}>
              <Text style={styles.title}>{device.option.name}</Text>
              <Text style={styles.status}>CONNECTED</Text>
            </View>
          </View>

          <View style={styles.actions}>
            <Pressable
              onPress={handleResync}
              disabled={actionPending}
              style={({ pressed }) => [styles.button, pressed && styles.pressed]}>
              {syncVitals.isPending ? (
                <ActivityIndicator color={Primitives.white} />
              ) : (
                <Text style={styles.buttonText}>RESYNC DATA</Text>
              )}
            </Pressable>
            <Pressable
              onPress={handleDisconnect}
              disabled={actionPending}
              style={({ pressed }) => [styles.button, styles.disconnect, pressed && styles.pressed]}>
              {disconnectDevice.isPending ? (
                <ActivityIndicator color={c.danger} />
              ) : (
                <Text style={[styles.buttonText, styles.disconnectText]}>DISCONNECT</Text>
              )}
            </Pressable>
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
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
  },
  headerText: { gap: 3 },
  title: {
    ...Typography.title24,
    color: c.text,
  },
  status: {
    ...Typography.caps10,
    color: c.textSuccess,
  },
  actions: { gap: 12 },
  button: {
    borderRadius: Radius.md,
    paddingVertical: 18,
    alignItems: 'center',
    backgroundColor: c.buttonPrimary,
  },
  buttonText: {
    ...Typography.caps13,
    letterSpacing: 1,
    color: Primitives.white,
  },
  disconnect: {
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: c.danger,
  },
  disconnectText: { color: c.danger },
  pressed: { opacity: 0.8 },
});

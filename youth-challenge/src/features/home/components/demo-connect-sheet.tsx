import { ActivityIndicator, Alert, Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Radius, Typography } from '@/constants/theme';
import { useDemoConnect } from '@/features/devices/api/use-demo-connect';
import { DeviceOption } from '@/features/home/components/connect-device-sheet';

const c = Colors.light;

export function DemoConnectSheet({
  device,
  onClose,
}: {
  device: DeviceOption | null;
  onClose: () => void;
}) {
  const insets = useSafeAreaInsets();
  const demoConnect = useDemoConnect();

  const handleConnect = () => {
    if (!device || demoConnect.isPending) return;
    demoConnect.mutate(device.slugs[0], {
      onSuccess: (result) => {
        if (result.type === 'success') {
          onClose();
          Alert.alert(`${device.name} connected`, `A demo ${device.name} account is now connected.`);
          return;
        }
        const message =
          result.type === 'conflict'
            ? "This account already has a real device connected. A demo device can't be added alongside it — use a separate account."
            : result.type === 'notProvisioned'
              ? 'Your account is still being set up. Please try again in a moment.'
              : result.type === 'unauthorized'
                ? 'Your session has expired. Please sign in again.'
                : 'Could not connect the demo device. Please try again.';
        Alert.alert('Could not connect', message);
      },
    });
  };

  if (!device) return null;
  const Icon = device.icon;

  return (
    <Modal visible transparent animationType="slide" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose} />
      <View style={styles.container} pointerEvents="box-none">
        <View style={[styles.sheet, { paddingBottom: Math.max(insets.bottom, 20) }]}>
          <View style={styles.handle} />

          <View style={styles.header}>
            <Icon color={c.text} size={28} />
            <View style={styles.headerText}>
              <Text style={styles.title}>{device.name}</Text>
              <Text style={styles.status}>DEMO</Text>
            </View>
          </View>

          <Text style={styles.body}>
            Connect a demo {device.name} account with synthetic test data — no login required.
          </Text>

          <View style={styles.actions}>
            <Pressable
              onPress={onClose}
              style={({ pressed }) => [styles.button, styles.cancel, pressed && styles.pressed]}>
              <Text style={[styles.buttonText, styles.cancelText]}>CANCEL</Text>
            </Pressable>
            <Pressable
              onPress={handleConnect}
              disabled={demoConnect.isPending}
              style={({ pressed }) => [styles.button, styles.connect, pressed && styles.pressed]}>
              {demoConnect.isPending ? (
                <ActivityIndicator color={Primitives.white} />
              ) : (
                <Text style={[styles.buttonText, styles.connectText]}>CONNECT</Text>
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
    color: c.textSecondary,
  },
  body: {
    ...Typography.body14,
    color: c.text,
  },
  actions: {
    flexDirection: 'row',
    gap: 12,
  },
  button: {
    flex: 1,
    borderRadius: Radius.md,
    paddingVertical: 18,
    alignItems: 'center',
  },
  cancel: {
    backgroundColor: c.surfaceGrey,
  },
  connect: {
    backgroundColor: c.buttonPrimary,
  },
  buttonText: {
    ...Typography.caps13,
    letterSpacing: 1,
  },
  cancelText: {
    color: c.text,
  },
  connectText: {
    color: Primitives.white,
  },
  pressed: { opacity: 0.8 },
});

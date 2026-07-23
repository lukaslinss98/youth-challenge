import { Pressable, StyleSheet, Text, View } from 'react-native';

import { CloseIcon } from '@/components/nav-icons';
import { Primitives, Radius, Typography } from '@/constants/theme';

/** Translucent "glass" promo card prompting the user to connect a device. */
export function ConnectDeviceCard({ onConnect }: { onConnect?: () => void }) {
  return (
    <View style={styles.card}>
      <View style={styles.top}>
        <View style={styles.iconTile} />
        <View style={styles.copy}>
          <Text style={styles.title}>Connect your devices</Text>
          <Text style={styles.body}>Unlock more insights by connecting your wearable devices</Text>
        </View>
        <Pressable hitSlop={8} style={styles.close}>
          <CloseIcon color="rgba(255,255,255,0.85)" size={22} />
        </Pressable>
      </View>

      <Pressable
        onPress={onConnect}
        style={({ pressed }) => [styles.button, pressed && styles.pressed]}>
        <Text style={styles.buttonText}>CONNECT A DEVICE</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    borderRadius: Radius.lg,
    padding: 20,
    gap: 20,
    backgroundColor: 'rgba(255,255,255,0.08)',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.12)',
  },
  top: {
    flexDirection: 'row',
    gap: 16,
  },
  iconTile: {
    width: 56,
    height: 56,
    borderRadius: Radius.md,
    backgroundColor: Primitives.orange,
  },
  copy: { flex: 1, gap: 4 },
  title: {
    ...Typography.title18,
    color: Primitives.white,
  },
  body: {
    ...Typography.body14,
    color: 'rgba(255,255,255,0.65)',
  },
  close: {
    paddingLeft: 4,
  },
  button: {
    borderRadius: Radius.md,
    paddingVertical: 16,
    alignItems: 'center',
    backgroundColor: 'rgba(255,255,255,0.10)',
    borderWidth: 1,
    borderColor: 'rgba(255,255,255,0.12)',
  },
  buttonText: {
    ...Typography.caps13,
    color: Primitives.white,
    letterSpacing: 1,
  },
  pressed: { opacity: 0.7 },
});

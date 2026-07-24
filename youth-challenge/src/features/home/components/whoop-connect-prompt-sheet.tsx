import { Image, Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Radius, Typography } from '@/constants/theme';

const c = Colors.light;

/** Confirmation sheet asking the user to start the Whoop connect flow. */
export function WhoopConnectPromptSheet({
  visible,
  onCancel,
  onContinue,
  onDismiss,
}: {
  visible: boolean;
  onCancel: () => void;
  onContinue: () => void;
  onDismiss?: () => void;
}) {
  const insets = useSafeAreaInsets();

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={onCancel}
      onDismiss={onDismiss}>
      <Pressable style={styles.backdrop} onPress={onCancel} />
      <View style={styles.container} pointerEvents="box-none">
        <View style={[styles.sheet, { paddingBottom: Math.max(insets.bottom, 20) }]}>
          <View style={styles.handle} />

          <View style={styles.imageCard}>
            <Image
              source={require('@/assets/images/whoop.png')}
              style={styles.image}
              resizeMode="cover"
            />
          </View>

          <View style={styles.copy}>
            <Text style={styles.title}>Connect your Whoop in a few steps</Text>
            <Text style={styles.body}>
              Select your device, log in securely, and confirm what data to share. You&apos;ll return
              here once setup is complete.
            </Text>
          </View>

          <View style={styles.actions}>
            <Pressable
              onPress={onCancel}
              style={({ pressed }) => [styles.button, styles.cancel, pressed && styles.pressed]}>
              <Text style={[styles.buttonText, styles.cancelText]}>CANCEL</Text>
            </Pressable>
            <Pressable
              onPress={onContinue}
              style={({ pressed }) => [styles.button, styles.continue, pressed && styles.pressed]}>
              <Text style={[styles.buttonText, styles.continueText]}>CONTINUE</Text>
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
  imageCard: {
    borderRadius: Radius.md,
    overflow: 'hidden',
    aspectRatio: 337 / 370,
    backgroundColor: '#E3EAE5',
  },
  image: {
    width: '100%',
    height: '100%',
  },
  copy: { gap: 8 },
  title: {
    ...Typography.title24,
    color: c.text,
  },
  body: {
    ...Typography.body14,
    color: c.textSecondary,
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
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: c.borderStrong,
  },
  continue: {
    backgroundColor: c.buttonPrimary,
  },
  buttonText: {
    ...Typography.caps13,
    letterSpacing: 1,
  },
  cancelText: { color: c.text },
  continueText: { color: Primitives.white },
  pressed: { opacity: 0.8 },
});

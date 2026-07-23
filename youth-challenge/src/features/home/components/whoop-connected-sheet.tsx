import { Image, Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Radius, Typography } from '@/constants/theme';
import { WhoopIcon } from '@/features/home/components/device-icons';

const c = Colors.light;

/** Success sheet shown after the user selects Whoop in the device picker. */
export function WhoopConnectedSheet({
  visible,
  onClose,
  onNext,
}: {
  visible: boolean;
  onClose: () => void;
  onNext?: () => void;
}) {
  const insets = useSafeAreaInsets();

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose}>
      <Pressable style={styles.backdrop} onPress={onClose} />
      <View style={styles.container} pointerEvents="box-none">
        <View style={[styles.sheet, { paddingBottom: Math.max(insets.bottom, 20) }]}>
          <View style={styles.handle} />

          <View style={styles.headerRow}>
            <Text style={styles.success}>SUCCESS</Text>
            <View style={styles.betaTag}>
              <Text style={styles.betaText}>BETA</Text>
            </View>
          </View>

          <Text style={styles.title}>Your Whoop is connected successfully!</Text>

          <View style={styles.imageCard}>
            <Image
              source={require('@/assets/images/whoop.png')}
              style={styles.image}
              resizeMode="cover"
            />
            <View style={styles.chip}>
              <WhoopIcon color={c.text} size={18} />
              <View>
                <Text style={styles.chipName}>Whoop</Text>
                <Text style={styles.chipStatus}>CONNECTED</Text>
              </View>
            </View>
          </View>

          <Pressable
            onPress={onNext ?? onClose}
            style={({ pressed }) => [styles.nextButton, pressed && styles.pressed]}>
            <Text style={styles.nextText}>NEXT</Text>
          </Pressable>
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
  headerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  success: {
    ...Typography.caps10,
    color: c.textSuccess,
  },
  betaTag: {
    backgroundColor: '#F1F1F1',
    borderRadius: Radius.pill,
    paddingHorizontal: 10,
    paddingVertical: 4,
  },
  betaText: {
    ...Typography.caps10,
    color: c.textSecondary,
  },
  title: {
    ...Typography.title24,
    color: c.text,
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
  chip: {
    position: 'absolute',
    left: 16,
    top: '44%',
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: Radius.md,
    backgroundColor: 'rgba(255,255,255,0.65)',
  },
  chipName: {
    ...Typography.title14,
    color: c.text,
  },
  chipStatus: {
    ...Typography.caps10,
    color: c.textSuccess,
  },
  nextButton: {
    backgroundColor: c.buttonPrimary,
    borderRadius: Radius.md,
    paddingVertical: 18,
    alignItems: 'center',
  },
  nextText: {
    ...Typography.caps13,
    color: Primitives.white,
    letterSpacing: 1,
  },
  pressed: { opacity: 0.8 },
});

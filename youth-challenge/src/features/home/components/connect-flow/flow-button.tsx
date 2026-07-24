import { ActivityIndicator, Pressable, StyleSheet, Text } from 'react-native';

import { Colors, Primitives, Radius, Typography } from '@/constants/theme';

const c = Colors.light;

export function FlowButton({
  label,
  onPress,
  variant = 'primary',
  pending = false,
  disabled = false,
}: {
  label: string;
  onPress: () => void;
  variant?: 'primary' | 'outlined';
  pending?: boolean;
  disabled?: boolean;
}) {
  const outlined = variant === 'outlined';
  return (
    <Pressable
      onPress={onPress}
      disabled={disabled || pending}
      style={({ pressed }) => [styles.button, outlined && styles.outlined, pressed && styles.pressed]}>
      {pending ? (
        <ActivityIndicator color={outlined ? c.text : Primitives.white} />
      ) : (
        <Text style={[styles.label, outlined && styles.outlinedLabel]}>{label}</Text>
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    flex: 1,
    height: 50,
    borderRadius: Radius.md,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: c.buttonPrimary,
  },
  outlined: {
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: c.borderStrong,
  },
  pressed: { opacity: 0.8 },
  label: {
    ...Typography.caps13,
    letterSpacing: 1,
    color: Primitives.white,
  },
  outlinedLabel: { color: c.text },
});

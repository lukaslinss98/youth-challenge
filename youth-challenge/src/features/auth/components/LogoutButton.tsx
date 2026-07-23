import { Pressable, StyleSheet } from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { Spacing } from '@/constants/theme';

import { useSessionStore } from '../store/session-store';

export function LogoutButton() {
  const clearSession = useSessionStore((state) => state.clear);

  return (
    <Pressable
      onPress={clearSession}
      hitSlop={8}
      style={({ pressed }) => [styles.button, pressed && styles.pressed]}>
      <ThemedText type="link">Log out</ThemedText>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    paddingHorizontal: Spacing.three,
  },
  pressed: {
    opacity: 0.7,
  },
});

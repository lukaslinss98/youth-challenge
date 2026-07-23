import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Primitives, Radius, Typography } from '@/constants/theme';
import { useSessionStore } from '@/features/auth/store/session-store';

const c = Colors.light;

/** User / settings page. Renders profile info with a logout action pinned to
 * the bottom; clearing the session flips the auth guard back to login. */
export function SettingsScreen() {
  const insets = useSafeAreaInsets();
  const user = useSessionStore((state) => state.user);
  const clearSession = useSessionStore((state) => state.clear);

  const username = user?.username?.trim();
  const name = username || 'Add your name';
  const initial = (username?.[0] ?? 'S').toUpperCase();

  return (
    <View style={styles.root}>
      <View style={styles.profile}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>{initial}</Text>
        </View>
        <Text style={styles.name}>{name}</Text>
        <Text style={styles.subtitle}>{user?.email ?? 'YOU(th) account'}</Text>
      </View>

      <View style={styles.spacer} />

      <Pressable
        onPress={clearSession}
        style={({ pressed }) => [
          styles.logout,
          { marginBottom: Math.max(insets.bottom, 16) },
          pressed && styles.pressed,
        ]}>
        <Text style={styles.logoutText}>Log out</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: c.appBackground,
    paddingHorizontal: 24,
    paddingTop: 24,
  },
  profile: {
    alignItems: 'center',
    gap: 8,
  },
  avatar: {
    width: 88,
    height: 88,
    borderRadius: 44,
    backgroundColor: c.surfaceGrey,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 8,
  },
  avatarText: {
    ...Typography.title40,
    color: Primitives.black,
  },
  name: {
    ...Typography.title24,
    color: c.text,
  },
  subtitle: {
    ...Typography.body14,
    color: c.textSecondary,
  },
  spacer: { flex: 1 },
  logout: {
    borderRadius: Radius.md,
    paddingVertical: 16,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: c.danger,
  },
  logoutText: {
    ...Typography.title16,
    color: c.danger,
  },
  pressed: { opacity: 0.7 },
});

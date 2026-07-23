import { Pressable, StyleSheet, Text, View } from 'react-native';

import { EditIcon } from '@/components/nav-icons';
import { Primitives, Typography } from '@/constants/theme';

type HomeHeaderProps = {
  name?: string;
  initial?: string;
  onPressAvatar?: () => void;
};

/** Top bar: "welcome back" greeting, editable name, and a tappable avatar. */
export function HomeHeader({ name = 'Add your name', initial = 'S', onPressAvatar }: HomeHeaderProps) {
  return (
    <View style={styles.row}>
      <View style={styles.greeting}>
        <Text style={styles.surtitle}>WELCOME BACK,</Text>
        <View style={styles.nameRow}>
          <Text style={styles.name}>{name}</Text>
          <EditIcon color="rgba(255,255,255,0.85)" size={18} />
        </View>
      </View>
      <Pressable
        onPress={onPressAvatar}
        hitSlop={8}
        accessibilityRole="button"
        accessibilityLabel="Open profile and settings"
        style={({ pressed }) => [styles.avatar, pressed && styles.pressed]}>
        <Text style={styles.avatarText}>{initial}</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
  },
  greeting: { gap: 6, flexShrink: 1 },
  surtitle: {
    ...Typography.caps12,
    color: 'rgba(255,255,255,0.7)',
  },
  nameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  name: {
    ...Typography.title24,
    color: Primitives.white,
  },
  avatar: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: Primitives.grey3,
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarText: {
    ...Typography.title18,
    color: Primitives.black,
  },
  pressed: { opacity: 0.7 },
});

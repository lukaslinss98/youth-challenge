import { Image, ImageSourcePropType, Pressable, StyleSheet, Text, View } from 'react-native';

import { Colors, Primitives, Typography } from '@/constants/theme';

const c = Colors.light;

export function ActionCard({
  variant = 'dark',
  children,
}: {
  variant?: 'dark' | 'light';
  children: React.ReactNode;
}) {
  return <View style={[styles.card, variant === 'light' && styles.cardLight]}>{children}</View>;
}

export function ActionCardButton({
  label,
  onPress,
  variant = 'dark',
}: {
  label: string;
  onPress?: () => void;
  variant?: 'dark' | 'light';
}) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [
        styles.button,
        variant === 'light' && styles.buttonLight,
        pressed && styles.pressed,
      ]}>
      <Text style={[styles.buttonText, variant === 'light' && styles.buttonTextLight]}>{label}</Text>
    </Pressable>
  );
}

export function ActionCardIcon({ source, tint }: { source: ImageSourcePropType; tint: string }) {
  return (
    <View style={styles.icon}>
      <Image source={source} style={styles.iconImage} resizeMode="cover" />
      <View style={[styles.iconTint, { backgroundColor: tint }]} />
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    height: 155,
    borderRadius: 18,
    borderWidth: 0.5,
    borderColor: Primitives.white,
    backgroundColor: 'rgba(27,27,27,0.2)',
    padding: 16,
    justifyContent: 'space-between',
  },
  cardLight: {
    backgroundColor: c.surface,
    borderColor: c.divider,
  },
  button: {
    backgroundColor: 'rgba(255,255,255,0.13)',
    borderRadius: 9,
    paddingVertical: 14,
    paddingHorizontal: 12,
    alignItems: 'center',
  },
  buttonLight: {
    backgroundColor: c.appBackground,
  },
  pressed: { opacity: 0.8 },
  buttonText: {
    ...Typography.caps12,
    color: Primitives.white,
  },
  buttonTextLight: {
    color: c.text,
  },
  icon: {
    width: 48,
    height: 48,
    borderRadius: 20,
    overflow: 'hidden',
  },
  iconImage: {
    width: '100%',
    height: '100%',
  },
  iconTint: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    mixBlendMode: 'saturation',
  },
});

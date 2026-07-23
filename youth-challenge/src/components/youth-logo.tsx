import { Image, type ImageStyle } from 'expo-image';
import { StyleSheet, type StyleProp } from 'react-native';

import { useTheme } from '@/hooks/use-theme';

const ASPECT_RATIO = 895 / 148;

type YouthLogoProps = {
  /** Rendered width in px; height is derived from the wordmark aspect ratio. */
  width?: number;
  style?: StyleProp<ImageStyle>;
};

/**
 * The YOU(th) wordmark. The source art is a solid dark shape on a transparent
 * background, so we tint it with the theme text color to stay legible in both
 * light and dark mode.
 */
export function YouthLogo({ width = 200, style }: YouthLogoProps) {
  const theme = useTheme();

  return (
    <Image
      source={require('@/assets/images/youth_logo_dark.png')}
      tintColor={theme.text}
      contentFit="contain"
      style={[styles.logo, { width, height: width / ASPECT_RATIO }, style]}
    />
  );
}

const styles = StyleSheet.create({
  logo: {
    alignSelf: 'center',
  },
});

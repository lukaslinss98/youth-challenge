/**
 * Below are the colors that are used in the app. The colors are defined in the light and dark mode.
 * There are many other ways to style your app. For example, [Nativewind](https://www.nativewind.dev/), [Tamagui](https://tamagui.dev/), [unistyles](https://reactnativeunistyles.vercel.app), etc.
 */

import '@/global.css';

import { Platform } from 'react-native';

/**
 * Brand primitives from the YOU(th) Figma sheet. Reference these via the
 * semantic `Colors` tokens below rather than hard-coding hex values in
 * components.
 */
export const Primitives = {
  black: '#1B1B1B',
  grey1: '#8F8F8F',
  grey2: '#DEDEDE',
  grey3: '#F4F3F3',
  white: '#FFFFFF',
  green: '#0ED187',
  blue: '#3422FF',
  pink: '#F54EF0',
  orange: '#FF8811',
  red: '#EC4444',
  transparent: 'transparent',
} as const;

/**
 * Semantic color roles, resolved from the YOU(th) Figma "Youth App" variable
 * collection (read via the Figma MCP `get_variable_defs`). Prefer these over
 * `Primitives` in components. Keys are identical across `light`/`dark` so the
 * `ThemeColor` union stays exhaustive.
 *
 * Design-system notes:
 * - The app surface is grey (`Surface/background` = grey3); cards sit on top in
 *   white (`Surface/card`). Use `appBackground` for screens, `surface` for cards.
 * - Primary CTAs are black (`Button/primary`), not blue. Blue is an accent only.
 */
export const Colors = {
  light: {
    // text
    text: Primitives.black, // Text/Primary
    textSecondary: Primitives.grey1, // Text/Secondary
    textDisabled: Primitives.grey2, // Text/Disabled
    textInverse: Primitives.white, // Text/Light (on dark)
    textSuccess: Primitives.green, // Text/Good
    // surfaces
    background: Primitives.white,
    appBackground: Primitives.grey3, // Surface/background
    surface: Primitives.white, // Surface/card
    surfaceGrey: Primitives.grey2, // Surface/Grey
    backgroundElement: Primitives.grey3,
    backgroundSelected: Primitives.grey2,
    // borders
    divider: Primitives.grey2, // Border/divider
    border: Primitives.grey2,
    borderStrong: Primitives.black, // Border/dark-outline
    // actions
    tint: Primitives.blue,
    buttonPrimary: Primitives.black, // Button/primary
    buttonPrimaryText: Primitives.white,
    // status / accents
    success: Primitives.green,
    warning: Primitives.orange,
    danger: Primitives.red,
    accent: Primitives.pink,
    // translucent dark nav (Surface/dark nav background, Border/dark nav outline)
    navSurface: 'rgba(48,48,48,0.20)',
    navOutline: 'rgba(255,255,255,0.10)',
  },
  dark: {
    text: Primitives.white,
    textSecondary: Primitives.grey1,
    textDisabled: '#5A5A5A',
    textInverse: Primitives.black,
    textSuccess: Primitives.green,
    background: Primitives.black,
    appBackground: Primitives.black,
    surface: '#2A2A2A',
    surfaceGrey: '#3A3A3A',
    backgroundElement: '#2A2A2A',
    backgroundSelected: '#3A3A3A',
    divider: '#3A3A3A',
    border: '#3A3A3A',
    borderStrong: Primitives.white,
    tint: Primitives.blue,
    buttonPrimary: Primitives.white,
    buttonPrimaryText: Primitives.black,
    success: Primitives.green,
    warning: Primitives.orange,
    danger: Primitives.red,
    accent: Primitives.pink,
    navSurface: 'rgba(48,48,48,0.20)',
    navOutline: 'rgba(255,255,255,0.10)',
  },
} as const;

export type ThemeColor = keyof typeof Colors.light & keyof typeof Colors.dark;

export const Fonts = Platform.select({
  ios: {
    /** iOS `UIFontDescriptorSystemDesignDefault` */
    sans: 'system-ui',
    /** iOS `UIFontDescriptorSystemDesignSerif` */
    serif: 'ui-serif',
    /** iOS `UIFontDescriptorSystemDesignRounded` */
    rounded: 'ui-rounded',
    /** iOS `UIFontDescriptorSystemDesignMonospaced` */
    mono: 'ui-monospace',
  },
  default: {
    sans: 'normal',
    serif: 'serif',
    rounded: 'normal',
    mono: 'monospace',
  },
  web: {
    sans: 'var(--font-display)',
    serif: 'var(--font-serif)',
    rounded: 'var(--font-rounded)',
    mono: 'var(--font-mono)',
  },
});

/**
 * Brand font families from the Figma design system. These are custom fonts —
 * they must be bundled and registered before use (see note below), otherwise
 * React Native silently falls back to the system font.
 *
 *   display → PP Neue Montreal  (titles & body)
 *   heading → Averta Std        (bold surtitles)
 *   mono    → IBM Plex Mono     (caps labels / code) — free via @expo-google-fonts/ibm-plex-mono
 *
 * TODO(fonts): add the .otf/.ttf files and load them with `useFonts` /
 * `expo-font` in the root layout, keying each weight to these family names.
 * PP Neue Montreal and Averta Std are commercial fonts (licensed files needed);
 * IBM Plex Mono is open source.
 */
export const FontFamily = {
  display: 'PP Neue Montreal',
  heading: 'Averta Std',
  mono: 'IBM Plex Mono',
} as const;

/**
 * Type ramp resolved from the Figma "Youth App" text variables. `letterSpacing`
 * is in points (matches Figma px). Spread these into a Text `style`.
 */
export const Typography = {
  // PP Neue Montreal · Medium (500)
  title40: { fontFamily: FontFamily.display, fontWeight: '500', fontSize: 40, lineHeight: 44, letterSpacing: -1 },
  title24: { fontFamily: FontFamily.display, fontWeight: '500', fontSize: 24, lineHeight: 30, letterSpacing: -0.4 },
  title18: { fontFamily: FontFamily.display, fontWeight: '500', fontSize: 18, lineHeight: 24, letterSpacing: -0.2 },
  title16: { fontFamily: FontFamily.display, fontWeight: '500', fontSize: 16, lineHeight: 22, letterSpacing: 0 },
  title14: { fontFamily: FontFamily.display, fontWeight: '500', fontSize: 14, lineHeight: 20, letterSpacing: 0 },
  title12: { fontFamily: FontFamily.display, fontWeight: '500', fontSize: 12, lineHeight: 16, letterSpacing: 0 },
  // PP Neue Montreal · Regular (400)
  body14: { fontFamily: FontFamily.display, fontWeight: '400', fontSize: 14, lineHeight: 20, letterSpacing: 0 },
  body12: { fontFamily: FontFamily.display, fontWeight: '400', fontSize: 12, lineHeight: 16, letterSpacing: 0 },
  // Averta Std · Bold (700)
  surtitle: { fontFamily: FontFamily.heading, fontWeight: '700', fontSize: 14, lineHeight: 22, letterSpacing: 0 },
  // IBM Plex Mono · Medium (500) — small-caps style labels
  caps13: { fontFamily: FontFamily.mono, fontWeight: '500', fontSize: 13, lineHeight: 20, letterSpacing: 0 },
  caps12: { fontFamily: FontFamily.mono, fontWeight: '500', fontSize: 12, lineHeight: 16, letterSpacing: 0 },
  caps10: { fontFamily: FontFamily.mono, fontWeight: '500', fontSize: 10, lineHeight: 12, letterSpacing: 1 },
} as const;

/**
 * Corner radii. `pill` (`border-radius-rounded`) is the only value defined as a
 * Figma variable; the smaller steps are conventional defaults — refine against
 * specific components as needed.
 */
export const Radius = {
  sm: 8,
  md: 12,
  lg: 20,
  pill: 800,
} as const;

export const Spacing = {
  half: 2,
  one: 4,
  two: 8,
  three: 16,
  four: 24,
  five: 32,
  six: 64,
} as const;

export const BottomTabInset = Platform.select({ ios: 50, android: 80 }) ?? 0;
export const MaxContentWidth = 800;

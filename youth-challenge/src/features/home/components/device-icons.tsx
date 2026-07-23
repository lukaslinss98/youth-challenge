import Svg, { Circle, Path, Polyline } from 'react-native-svg';

type IconProps = { color?: string; size?: number };

/** Approximated brand marks — swap for official assets when available. */

export function AppleIcon({ color = '#1B1B1B', size = 22 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24">
      <Path
        d="M15.8 3c.1.9-.3 1.8-.8 2.4-.6.7-1.5 1.2-2.4 1.1-.1-.9.3-1.8.8-2.3.6-.7 1.6-1.2 2.4-1.2Z"
        fill={color}
      />
      <Path
        d="M18.6 16.6c-.3.7-.5 1-.9 1.7-.6 1-1.5 2.3-2.6 2.3-1 0-1.2-.6-2.5-.6s-1.6.6-2.5.6c-1.1 0-1.9-1.1-2.5-2.1-1.7-2.7-1.9-5.9-.8-7.6.8-1.2 1.9-1.9 3.1-1.9 1.1 0 1.8.6 2.7.6.9 0 1.4-.6 2.7-.6 1 0 2.1.5 2.8 1.5-2.5 1.4-2.1 4.9.3 5.6Z"
        fill={color}
      />
    </Svg>
  );
}

export function OuraIcon({ color = '#1B1B1B', size = 22 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24">
      <Circle cx="12" cy="12" r="7.5" stroke={color} strokeWidth={2} fill="none" />
    </Svg>
  );
}

export function GarminIcon({ color = '#1B1B1B', size = 22 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24">
      <Path d="M12 4 L20 19 H4 Z" fill={color} />
    </Svg>
  );
}

export function WhoopIcon({ color = '#1B1B1B', size = 22 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24">
      <Polyline
        points="4,6 8,18 12,9 16,18 20,6"
        stroke={color}
        strokeWidth={2}
        strokeLinecap="round"
        strokeLinejoin="round"
        fill="none"
      />
    </Svg>
  );
}

export function ChevronRight({ color = '#1B1B1B', size = 20 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24">
      <Path
        d="M9 6l6 6-6 6"
        stroke={color}
        strokeWidth={2}
        strokeLinecap="round"
        strokeLinejoin="round"
        fill="none"
      />
    </Svg>
  );
}

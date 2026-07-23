import Svg, { Circle, Line, Path, Rect } from 'react-native-svg';

type IconProps = { color: string; size?: number };

/** Rounded-square "face" glyph used for the Home tab. */
export function HomeIcon({ color, size = 24 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <Path
        d="M5 9a5 5 0 0 1 5-5h4a5 5 0 0 1 5 5v10a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V9Z"
        fill={color}
      />
      <Rect x="9" y="12.5" width="6" height="2" rx="1" fill="#FFFFFF" />
    </Svg>
  );
}

/** Three ascending bars for the Data tab. */
export function DataIcon({ color, size = 24 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <Line x1="6" y1="15" x2="6" y2="19" stroke={color} strokeWidth={2} strokeLinecap="round" />
      <Line x1="12" y1="9" x2="12" y2="19" stroke={color} strokeWidth={2} strokeLinecap="round" />
      <Line x1="18" y1="5" x2="18" y2="19" stroke={color} strokeWidth={2} strokeLinecap="round" />
    </Svg>
  );
}

/** Clipboard with a check for the Plan tab. */
export function PlanIcon({ color, size = 24 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <Rect x="5" y="5" width="14" height="16" rx="2.5" stroke={color} strokeWidth={2} />
      <Rect x="9" y="3" width="6" height="3.5" rx="1.5" stroke={color} strokeWidth={2} />
      <Path d="M8.5 13.5l2.5 2.5 4.5-5" stroke={color} strokeWidth={2} strokeLinecap="round" strokeLinejoin="round" />
    </Svg>
  );
}

/** Speech bubble with a spark for the Chat tab. */
export function ChatIcon({ color, size = 24 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <Path
        d="M4 11a6 6 0 0 1 6-6h4a6 6 0 0 1 6 6 6 6 0 0 1-6 6h-3l-4 3v-3.4A6 6 0 0 1 4 11Z"
        stroke={color}
        strokeWidth={2}
        strokeLinejoin="round"
      />
      <Path d="M12 8.5l.8 1.7 1.7.8-1.7.8-.8 1.7-.8-1.7-1.7-.8 1.7-.8.8-1.7Z" fill={color} />
    </Svg>
  );
}

/** Plus glyph for the center FAB. */
export function PlusIcon({ color, size = 28 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <Line x1="12" y1="6" x2="12" y2="18" stroke={color} strokeWidth={2.5} strokeLinecap="round" />
      <Line x1="6" y1="12" x2="18" y2="12" stroke={color} strokeWidth={2.5} strokeLinecap="round" />
    </Svg>
  );
}

/** Pencil-in-square used next to the editable name in the header. */
export function EditIcon({ color, size = 18 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <Path d="M11 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-5" stroke={color} strokeWidth={2} strokeLinecap="round" />
      <Path d="M18 3.5l2.5 2.5L13 13.5l-3 .5.5-3L18 3.5Z" stroke={color} strokeWidth={2} strokeLinejoin="round" />
    </Svg>
  );
}

/** Close (X) glyph for the dismissable card. */
export function CloseIcon({ color, size = 22 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <Line x1="6" y1="6" x2="18" y2="18" stroke={color} strokeWidth={2} strokeLinecap="round" />
      <Line x1="18" y1="6" x2="6" y2="18" stroke={color} strokeWidth={2} strokeLinecap="round" />
    </Svg>
  );
}

/** Simple filled dot used by the header avatar fallback ring. */
export function Dot({ color, size = 6 }: IconProps) {
  return (
    <Svg width={size} height={size} viewBox="0 0 6 6">
      <Circle cx="3" cy="3" r="3" fill={color} />
    </Svg>
  );
}

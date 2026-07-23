import { StyleSheet, Text, View } from 'react-native';
import Svg, { Path } from 'react-native-svg';

import { Primitives, Typography } from '@/constants/theme';

const SIZE = 300;
const STROKE = 6;
const R = (SIZE - STROKE) / 2;
const CX = SIZE / 2;
const CY = SIZE / 2;

function pointAt(deg: number) {
  const rad = (deg * Math.PI) / 180;
  return { x: CX + R * Math.cos(rad), y: CY - R * Math.sin(rad) };
}

const start = pointAt(180);
const end = pointAt(0);
const nub = pointAt(5);

export function HealthScoreGauge({ score = 15, label = 'Optimal' }: { score?: number; label?: string }) {
  return (
    <View style={styles.wrap}>
      <Svg width={SIZE} height={CY + STROKE} viewBox={`0 0 ${SIZE} ${CY + STROKE}`}>
        <Path
          d={`M ${start.x} ${start.y} A ${R} ${R} 0 0 1 ${end.x} ${end.y}`}
          stroke="rgba(255,255,255,0.25)"
          strokeWidth={STROKE}
          strokeLinecap="round"
          fill="none"
        />
        <Path
          d={`M ${start.x} ${start.y} A ${R} ${R} 0 0 1 ${nub.x} ${nub.y}`}
          stroke="rgba(255,255,255,0.7)"
          strokeWidth={STROKE}
          strokeLinecap="round"
          fill="none"
        />
      </Svg>

      <Text style={[styles.endLabel, styles.zero]}>0</Text>
      <Text style={[styles.endLabel, styles.hundred]}>100</Text>

      <View style={styles.center} pointerEvents="none">
        <Text style={styles.score}>{score}</Text>
        <Text style={styles.optimal}>{label}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    width: SIZE,
    height: 200,
    alignItems: 'center',
  },
  endLabel: {
    ...Typography.caps12,
    position: 'absolute',
    color: 'rgba(255,255,255,0.6)',
  },
  zero: { left: 34, top: CY - 20 },
  hundred: { right: 26, top: CY - 20 },
  center: {
    position: 'absolute',
    top: 70,
    alignItems: 'center',
  },
  score: {
    ...Typography.title40,
    color: Primitives.white,
    fontSize: 84,
    lineHeight: 88,
    letterSpacing: -2,
  },
  optimal: {
    ...Typography.title18,
    color: Primitives.white,
    marginTop: 2,
  },
});

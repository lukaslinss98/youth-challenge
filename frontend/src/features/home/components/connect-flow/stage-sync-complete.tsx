import { LinearGradient } from 'expo-linear-gradient';
import { Image, StyleSheet, Text, View } from 'react-native';

import { Colors, Typography } from '@/constants/theme';

const c = Colors.light;

export function StageSyncComplete() {
  return (
    <View style={styles.hero}>
      <LinearGradient
        colors={['#A3A3A3', '#D7D7D7', '#F6F6F6']}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={StyleSheet.absoluteFill}
      />
      <LinearGradient
        colors={['rgba(224,255,243,0.2)', 'rgba(113,124,120,0.2)']}
        style={StyleSheet.absoluteFill}
      />
      <Image
        source={require('@/assets/images/sync-complete-hero.png')}
        style={styles.image}
        resizeMode="cover"
      />
      <View style={styles.labels}>
        <Text style={styles.area}>CARDIOVASCULAR</Text>
        <View style={styles.metricRow}>
          <Text style={styles.metric}>HRV</Text>
          <View style={styles.tag}>
            <Text style={styles.tagText}>LAB & DIGITAL</Text>
          </View>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  hero: {
    flex: 1,
    borderRadius: 24,
    overflow: 'hidden',
  },
  image: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
  },
  labels: {
    position: 'absolute',
    left: 40,
    top: 20,
    gap: 4,
  },
  area: {
    ...Typography.caps12,
    color: c.text,
    opacity: 0.4,
    letterSpacing: 1,
  },
  metricRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  metric: {
    ...Typography.title24,
    color: c.text,
  },
  tag: {
    backgroundColor: 'rgba(222,222,222,0.5)',
    borderRadius: 4,
    padding: 4,
  },
  tagText: {
    ...Typography.caps10,
    color: '#67645E',
    letterSpacing: 0.5,
  },
});

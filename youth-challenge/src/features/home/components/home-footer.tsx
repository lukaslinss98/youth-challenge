import { Image, StyleSheet, Text, View } from 'react-native';

import { Colors, Typography } from '@/constants/theme';

const c = Colors.light;

export function HomeFooter() {
  return (
    <View style={styles.root}>
      <Text style={styles.label}>DISCLAIMER</Text>
      <Text style={styles.body}>
        It&apos;s important to note that while these biomarkers provide a comprehensive overview, they
        don&apos;t capture everything. A regular check-up with health professionals is recommended.{' '}
        <Text style={styles.link}>Learn more.</Text>
      </Text>
      <Image
        source={require('@/assets/images/youth_logo_dark.png')}
        style={styles.logo}
        resizeMode="contain"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    paddingVertical: 32,
    gap: 16,
  },
  label: {
    ...Typography.caps12,
    color: c.textSecondary,
  },
  body: {
    ...Typography.body14,
    fontSize: 12,
    lineHeight: 16,
    color: c.textSecondary,
  },
  link: {
    textDecorationLine: 'underline',
  },
  logo: {
    marginTop: 6,
    width: 120,
    height: 20,
    alignSelf: 'flex-start',
  },
});

import { ScrollView, StyleSheet, Text, View } from 'react-native';

import { Colors, Primitives, Typography } from '@/constants/theme';

const c = Colors.light;

export function StageSyncingBiomarkers({ biomarkers }: { biomarkers: string[] }) {
  return (
    <ScrollView
      style={styles.list}
      contentContainerStyle={styles.listContent}
      showsVerticalScrollIndicator={false}>
      {biomarkers.length === 0 ? (
        <View style={styles.card}>
          <Text style={styles.name}>Waiting for the first readings…</Text>
        </View>
      ) : (
        biomarkers.map((name) => (
          <View key={name} style={styles.card}>
            <Text style={styles.name} numberOfLines={1}>
              {name}
            </Text>
            <View style={styles.pill}>
              <View style={styles.pillBar} />
              <Text style={styles.pillText}>Synced</Text>
            </View>
          </View>
        ))
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  list: { flex: 1 },
  listContent: { gap: 12 },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    backgroundColor: c.surface,
    borderWidth: 1.5,
    borderColor: Primitives.white,
    borderRadius: 16,
    paddingVertical: 16,
    paddingHorizontal: 16,
  },
  name: {
    ...Typography.title16,
    color: c.text,
    flex: 1,
  },
  pill: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  pillBar: {
    width: 5,
    height: 12,
    borderRadius: 9,
    backgroundColor: c.textSuccess,
  },
  pillText: {
    ...Typography.body14,
    color: c.text,
  },
});

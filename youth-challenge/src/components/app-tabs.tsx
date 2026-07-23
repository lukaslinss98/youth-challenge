import { LinearGradient } from 'expo-linear-gradient';
import { TabList, Tabs, TabSlot, TabTrigger, type TabTriggerSlotProps } from 'expo-router/ui';
import { forwardRef } from 'react';
import { Pressable, StyleSheet, Text, View, type GestureResponderEvent } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { ChatIcon, DataIcon, HomeIcon, PlanIcon, PlusIcon } from '@/components/nav-icons';
import { Colors, Primitives, Typography } from '@/constants/theme';

const c = Colors.light;

export default function AppTabs() {
  return (
    <Tabs>
      <TabSlot style={styles.slot} />
      <TabList asChild>
        <BottomBar>
          <TabTrigger name="index" href="/" asChild>
            <NavItem label="Home" icon={HomeIcon} />
          </TabTrigger>
        </BottomBar>
      </TabList>
    </Tabs>
  );
}

/** The white nav bar. Home (the real route) is passed in as children; the other
 * items are presentational placeholders until their routes exist. */
function BottomBar({ children }: { children: React.ReactNode }) {
  const insets = useSafeAreaInsets();
  return (
    <View style={[styles.bar, { paddingBottom: Math.max(insets.bottom, 12) }]}>
      {children}
      <NavItem label="Data" icon={DataIcon} />
      <CenterFab />
      <NavItem label="Plan" icon={PlanIcon} />
      <NavItem label="Chat" icon={ChatIcon} />
    </View>
  );
}

type NavItemProps = Partial<TabTriggerSlotProps> & {
  label: string;
  icon: (p: { color: string; size?: number }) => React.ReactElement;
};

/** A single tab: icon + label. Receives `isFocused`/`onPress` when wrapped by a
 * TabTrigger (Home); otherwise renders as a static placeholder. */
const NavItem = forwardRef<View, NavItemProps>(({ label, icon: Icon, isFocused, onPress }, ref) => {
  const color = isFocused ? c.text : c.textSecondary;
  return (
    <Pressable
      ref={ref}
      onPress={onPress as (e: GestureResponderEvent) => void}
      style={styles.item}>
      <Icon color={color} size={24} />
      <Text style={[Typography.body12, styles.label, { color }]}>{label}</Text>
    </Pressable>
  );
});
NavItem.displayName = 'NavItem';

function CenterFab() {
  return (
    <View style={styles.fabWrap}>
      <LinearGradient
        colors={[Primitives.orange, '#B5642F']}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={styles.fab}>
        <PlusIcon color={Primitives.white} size={30} />
      </LinearGradient>
    </View>
  );
}

const styles = StyleSheet.create({
  slot: { flex: 1 },
  bar: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    backgroundColor: c.surface,
    paddingTop: 10,
    paddingHorizontal: 8,
  },
  item: {
    flex: 1,
    alignItems: 'center',
    gap: 4,
    paddingTop: 4,
  },
  label: {
    fontSize: 12,
  },
  fabWrap: {
    flex: 1,
    alignItems: 'center',
  },
  fab: {
    width: 60,
    height: 60,
    borderRadius: 20,
    marginTop: -18,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: Primitives.orange,
    shadowOpacity: 0.4,
    shadowRadius: 12,
    shadowOffset: { width: 0, height: 6 },
    elevation: 8,
  },
});

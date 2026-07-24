import { useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'expo-router';
import * as WebBrowser from 'expo-web-browser';
import { useState } from 'react';
import { Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Colors, Typography } from '@/constants/theme';
import { useDemoConnect } from '@/features/devices/api/use-demo-connect';
import { DEVICES_QUERY_KEY, useDevices } from '@/features/devices/api/use-devices';
import { useLinkToken } from '@/features/devices/api/use-link-token';
import { VITALS_QUERY_KEY, useVitals } from '@/features/devices/api/use-vitals';
import { metricLabel } from '@/features/devices/components/vital-reading-cards';
import { FlowButton } from '@/features/home/components/connect-flow/flow-button';
import { StageHeader } from '@/features/home/components/connect-flow/stage-header';
import {
  ConnectPrompt,
  DevicePicker,
} from '@/features/home/components/connect-flow/stage-select-device';
import { StageSyncComplete } from '@/features/home/components/connect-flow/stage-sync-complete';
import { StageSyncInfo } from '@/features/home/components/connect-flow/stage-sync-info';
import { StageSyncingBiomarkers } from '@/features/home/components/connect-flow/stage-syncing-biomarkers';
import { StoryProgress } from '@/features/home/components/connect-flow/story-progress';
import { DEMO_CAPABLE_SLUGS, DeviceOption } from '@/features/home/components/device-options';

const c = Colors.light;

const WHOOP_SLUGS = ['whoop', 'whoop_v2'];

type Stage = 1 | 2 | 3 | 4;

export function ConnectFlowSheet({
  visible,
  onClose,
  onSelectConnected,
}: {
  visible: boolean;
  onClose: () => void;
  onSelectConnected: (device: DeviceOption, provider: string) => void;
}) {
  const insets = useSafeAreaInsets();
  const router = useRouter();
  const queryClient = useQueryClient();
  const linkTokenMutation = useLinkToken();
  const demoConnect = useDemoConnect();
  const { data: devices } = useDevices();
  const [stage, setStage] = useState<Stage>(1);
  const [promptDevice, setPromptDevice] = useState<DeviceOption | null>(null);
  const [syncedDevice, setSyncedDevice] = useState<DeviceOption | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [linking, setLinking] = useState(false);
  const { data: readings } = useVitals({ pollMs: stage === 2 || stage === 3 ? 4000 : undefined });

  const statusBySlug = new Map((devices ?? []).map((device) => [device.provider, device.status]));
  const whoopConnected = WHOOP_SLUGS.some((slug) => statusBySlug.get(slug) === 'CONNECTED');
  const syncedSlugs: readonly string[] | null = syncedDevice ? syncedDevice.slugs : null;
  const biomarkers = [
    ...new Set(
      (readings ?? [])
        .filter((reading) => !syncedSlugs || syncedSlugs.includes(reading.provider))
        .map((reading) => metricLabel(reading.metric)),
    ),
  ];

  if (linking && whoopConnected && stage === 1) {
    setLinking(false);
    setSyncedDevice(promptDevice);
    setPromptDevice(null);
    setStage(2);
  }

  const reset = () => {
    setStage(1);
    setPromptDevice(null);
    setSyncedDevice(null);
    setError(null);
    setLinking(false);
  };

  const close = () => {
    reset();
    onClose();
  };

  const handleSelectDevice = (device: DeviceOption, connectedProvider: string | null) => {
    if (connectedProvider) {
      close();
      onSelectConnected(device, connectedProvider);
    } else {
      setPromptDevice(device);
    }
  };

  const handleConnectDemo = (device: DeviceOption) => {
    if (demoConnect.isPending) return;
    setError(null);
    demoConnect.mutate(device.slugs[0], {
      onSuccess: (result) => {
        if (result.type === 'success') {
          setSyncedDevice(device);
          setPromptDevice(null);
          setStage(2);
          return;
        }
        setError(
          result.type === 'conflict'
            ? "This account already has a real device connected. A demo device can't be added alongside it."
            : result.type === 'notProvisioned'
              ? 'Your account is still being set up. Please try again in a moment.'
              : result.type === 'unauthorized'
                ? 'Your session has expired. Please sign in again.'
                : 'Could not connect the demo device. Please try again.',
        );
      },
    });
  };

  const handleConnectWhoop = () => {
    if (linkTokenMutation.isPending) return;
    setError(null);
    linkTokenMutation.mutate('whoop', {
      onSuccess: async (result) => {
        if (result.type !== 'success') {
          setError(
            result.type === 'notProvisioned'
              ? 'Your account is still being set up. Please try again in a moment.'
              : result.type === 'unauthorized'
                ? 'Your session has expired. Please sign in again.'
                : result.message,
          );
          return;
        }
        try {
          await WebBrowser.openBrowserAsync(result.linkWebUrl);
        } catch {
          setError('Could not open the connection page. Please try again.');
          return;
        }
        queryClient.invalidateQueries({ queryKey: DEVICES_QUERY_KEY });
        queryClient.invalidateQueries({ queryKey: VITALS_QUERY_KEY });
        setLinking(true);
      },
    });
  };

  const handleContinue = (device: DeviceOption) => {
    if (device.name === 'Whoop') {
      handleConnectWhoop();
    } else if (DEMO_CAPABLE_SLUGS.includes(device.slugs[0])) {
      handleConnectDemo(device);
    }
  };

  const header = (() => {
    if (stage === 1) {
      const title = promptDevice
        ? `Connect your ${promptDevice.name} in a few steps`
        : 'Select a device';
      return (
        <StageHeader label="Connect" labelColor={c.textSecondary} step={1} steps={4} title={title} />
      );
    }
    const titles: Record<Exclude<Stage, 1>, string> = {
      2:
        biomarkers.length > 0
          ? `We are syncing ${biomarkers.length} biomarkers`
          : 'We are syncing your biomarkers',
      3: 'The full sync can take up to 5 minutes',
      4: 'You can check the biomarkers in the data section',
    };
    return <StageHeader label="Success" step={stage} steps={4} title={titles[stage]} />;
  })();

  const content = (() => {
    switch (stage) {
      case 1:
        if (promptDevice) {
          return <ConnectPrompt device={promptDevice} waiting={linking} error={error} />;
        }
        return <DevicePicker statusBySlug={statusBySlug} onSelect={handleSelectDevice} />;
      case 2:
        return <StageSyncingBiomarkers biomarkers={biomarkers} />;
      case 3:
        return <StageSyncInfo biomarkers={biomarkers} />;
      case 4:
        return <StageSyncComplete />;
    }
  })();

  const actions = (() => {
    switch (stage) {
      case 1: {
        if (!promptDevice) return null;
        const device = promptDevice;
        return (
          <>
            <FlowButton
              label="CANCEL"
              variant="outlined"
              onPress={() => {
                setPromptDevice(null);
                setError(null);
              }}
            />
            <FlowButton
              label="CONTINUE"
              onPress={() => handleContinue(device)}
              pending={linkTokenMutation.isPending || demoConnect.isPending}
            />
          </>
        );
      }
      case 2:
        return <FlowButton label="NEXT" onPress={() => setStage(3)} />;
      case 3:
        return <FlowButton label="NEXT" onPress={() => setStage(4)} />;
      case 4:
        return (
          <>
            <FlowButton
              label="MY DEVICES"
              variant="outlined"
              onPress={() => {
                close();
                router.push('/data');
              }}
            />
            <FlowButton label="CONTINUE" onPress={close} />
          </>
        );
    }
  })();

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={close}>
      <Pressable style={styles.backdrop} onPress={close} />
      <View style={styles.container} pointerEvents="box-none">
        <View style={[styles.flow, { paddingBottom: Math.max(insets.bottom, 12) }]}>
          <StoryProgress steps={4} active={stage - 1} />
          <View style={styles.card}>
            {header}
            <View style={styles.content}>{content}</View>
            {actions && <View style={styles.actions}>{actions}</View>}
            <View style={styles.betaTag}>
              <Text style={styles.betaText}>BETA</Text>
            </View>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  container: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  flow: {
    paddingHorizontal: 8,
  },
  card: {
    height: 580,
    maxHeight: '92%',
    borderRadius: 28,
    backgroundColor: '#F1F1F1',
    padding: 20,
    gap: 16,
  },
  content: {
    flex: 1,
  },
  actions: {
    flexDirection: 'row',
    gap: 8,
  },
  betaTag: {
    position: 'absolute',
    top: 20,
    right: 20,
    backgroundColor: c.textDisabled,
    borderRadius: 7,
    padding: 8,
  },
  betaText: {
    ...Typography.caps10,
    color: c.text,
  },
});

import { AppleIcon, OuraIcon, WhoopIcon } from '@/features/home/components/device-icons';

export const DEVICES = [
  {
    name: 'Apple Watch',
    icon: AppleIcon,
    slugs: ['apple_health_kit'],
    image: require('@/assets/images/apple_watch.png'),
  },
  {
    name: 'Oura',
    icon: OuraIcon,
    slugs: ['oura'],
    image: require('@/assets/images/oura.png'),
  },
  {
    name: 'Whoop',
    icon: WhoopIcon,
    slugs: ['whoop', 'whoop_v2'],
    image: require('@/assets/images/whoop.png'),
  },
] as const;

export type DeviceOption = (typeof DEVICES)[number];

export const DEMO_CAPABLE_SLUGS: string[] = ['oura', 'apple_health_kit'];

export function statusLabel(status: string) {
  if (status === 'PENDING') return 'CONNECTING…';
  if (status === 'ERROR') return 'CONNECTION FAILED';
  return status === 'CONNECTED' ? 'CONNECTED' : status.replace(/_/g, ' ');
}

import { useQuery } from '@tanstack/react-query';

import { useSessionStore } from '@/features/auth/store/session-store';
import { apiRequest } from '@/shared/api/client';

import type { Device } from '../types';

export const DEVICES_QUERY_KEY = ['devices'] as const;

const PENDING_POLL_CUTOFF_MS = 20 * 60 * 1000;

function hasFreshPending(devices: Device[] | undefined) {
  return (devices ?? []).some(
    (device) =>
      device.status === 'PENDING' &&
      Date.now() - new Date(device.updatedAt).getTime() < PENDING_POLL_CUTOFF_MS,
  );
}

export function useDevices() {
  const token = useSessionStore((state) => state.token);

  return useQuery({
    queryKey: DEVICES_QUERY_KEY,
    queryFn: () => apiRequest<Device[]>('/api/v1/devices'),
    enabled: !!token,
    refetchInterval: (query) => (hasFreshPending(query.state.data) ? 3000 : false),
  });
}

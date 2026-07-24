import { useMutation, useQueryClient } from '@tanstack/react-query';

import { ApiError, apiRequest } from '@/shared/api/client';

import { DEVICES_QUERY_KEY } from './use-devices';
import { VITALS_QUERY_KEY } from './use-vitals';

export type DemoConnectResult =
  | { type: 'success' }
  | { type: 'unauthorized' }
  | { type: 'notProvisioned' }
  | { type: 'conflict' }
  | { type: 'error' };

async function connectDemo(provider: string): Promise<DemoConnectResult> {
  try {
    await apiRequest<void>(`/api/v1/devices/demo-connect?provider=${encodeURIComponent(provider)}`, {
      method: 'POST',
    });
    return { type: 'success' };
  } catch (error) {
    if (error instanceof ApiError) {
      if (error.status === 401) return { type: 'unauthorized' };
      if (error.status === 409) return { type: 'notProvisioned' };
      if (error.status === 422) return { type: 'conflict' };
    }
    return { type: 'error' };
  }
}

export function useDemoConnect() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: connectDemo,
    onSuccess: (result) => {
      if (result.type === 'success') {
        queryClient.invalidateQueries({ queryKey: DEVICES_QUERY_KEY });
        queryClient.invalidateQueries({ queryKey: VITALS_QUERY_KEY });
      }
    },
  });
}

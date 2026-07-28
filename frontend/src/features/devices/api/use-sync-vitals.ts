import { useMutation, useQueryClient } from '@tanstack/react-query';

import { apiRequest } from '@/shared/api/client';

import { DEVICES_QUERY_KEY } from './use-devices';
import { VITALS_QUERY_KEY } from './use-vitals';

type SyncResult = { ingested: number };

export function useSyncVitals() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (provider?: string) =>
      apiRequest<SyncResult>(
        provider
          ? `/api/v1/vitals/sync?provider=${encodeURIComponent(provider)}`
          : '/api/v1/vitals/sync',
        { method: 'POST' },
      ),
    onSuccess: () =>
      Promise.all([
        queryClient.invalidateQueries({ queryKey: VITALS_QUERY_KEY }),
        queryClient.invalidateQueries({ queryKey: DEVICES_QUERY_KEY }),
      ]),
  });
}

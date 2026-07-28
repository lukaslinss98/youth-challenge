import { useMutation, useQueryClient } from '@tanstack/react-query';

import { apiRequest } from '@/shared/api/client';

import { DEVICES_QUERY_KEY } from './use-devices';

export function useDisconnectDevice() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (provider: string) =>
      apiRequest<void>(`/api/v1/devices/${encodeURIComponent(provider)}`, { method: 'DELETE' }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: DEVICES_QUERY_KEY });
    },
  });
}

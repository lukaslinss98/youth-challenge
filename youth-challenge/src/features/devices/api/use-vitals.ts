import { useQuery } from '@tanstack/react-query';

import { useSessionStore } from '@/features/auth/store/session-store';
import { apiRequest } from '@/shared/api/client';

import type { VitalReading } from '../types';

export const VITALS_QUERY_KEY = ['vitals'] as const;

export function useVitals() {
  const token = useSessionStore((state) => state.token);

  return useQuery({
    queryKey: VITALS_QUERY_KEY,
    queryFn: () => apiRequest<VitalReading[]>('/api/v1/vitals'),
    enabled: !!token,
  });
}

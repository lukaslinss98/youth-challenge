import { useMutation } from '@tanstack/react-query';

import { ApiError, apiRequest } from '@/shared/api/client';

import type { LinkTokenResult } from '../types';

type LinkTokenBody = { linkToken: string; linkWebUrl: string };

async function createLinkToken(provider: string): Promise<LinkTokenResult> {
  try {
    const body = await apiRequest<LinkTokenBody>(
      `/api/v1/devices/link-token?provider=${encodeURIComponent(provider)}`,
      { method: 'POST' },
    );
    return { type: 'success', linkToken: body.linkToken, linkWebUrl: body.linkWebUrl };
  } catch (error) {
    if (error instanceof ApiError) {
      if (error.status === 401) {
        return { type: 'unauthorized' };
      }
      if (error.status === 409) {
        return { type: 'notProvisioned' };
      }
      return { type: 'error', message: 'Something went wrong. Please try again.' };
    }
    return { type: 'error', message: 'Could not reach the server. Check your connection.' };
  }
}

export function useLinkToken() {
  return useMutation({ mutationFn: createLinkToken });
}

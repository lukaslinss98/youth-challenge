import { useMutation } from '@tanstack/react-query';

import { API_BASE_URL } from '@/shared/constants/api';

import type { LoginResult } from '../types';

type LoginInput = {
  email: string;
  password: string;
};

async function loginUser({ email, password }: LoginInput): Promise<LoginResult> {
  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}/api/v1/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
  } catch {
    return { type: 'error', message: 'Could not reach the server. Check your connection.' };
  }

  if (response.status === 200) {
    const body = await response.json();
    return { type: 'success', user: body.user, token: body.token };
  }

  if (response.status === 401) {
    return { type: 'invalidCredentials' };
  }

  return { type: 'error', message: 'Something went wrong. Please try again.' };
}

export function useLogin() {
  return useMutation({ mutationFn: loginUser });
}

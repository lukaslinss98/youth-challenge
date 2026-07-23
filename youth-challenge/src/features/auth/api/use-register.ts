import { useMutation } from '@tanstack/react-query';

import { API_BASE_URL } from '@/shared/constants/api';

import type { RegisterResult } from '../types';

type RegisterInput = {
  email: string;
  password: string;
};

async function registerUser({ email, password }: RegisterInput): Promise<RegisterResult> {
  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}/api/v1/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
  } catch {
    return { type: 'error', message: 'Could not reach the server. Check your connection.' };
  }

  if (response.status === 201) {
    const body = await response.json();
    return { type: 'success', user: body.user };
  }

  if (response.status === 409) {
    return { type: 'userExists' };
  }

  return { type: 'error', message: 'Something went wrong. Please try again.' };
}

export function useRegister() {
  return useMutation({ mutationFn: registerUser });
}

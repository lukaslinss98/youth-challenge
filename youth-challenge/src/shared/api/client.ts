import { useSessionStore } from '@/features/auth/store/session-store';
import { API_BASE_URL } from '@/shared/constants/api';

export class ApiError extends Error {
  constructor(readonly status: number) {
    super(`Request failed with status ${status}`);
    this.name = 'ApiError';
  }
}

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
  auth?: boolean;
};

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, auth = true } = options;

  const headers: Record<string, string> = {};
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }
  if (auth) {
    const token = useSessionStore.getState().token;
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (!response.ok) {
    throw new ApiError(response.status);
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

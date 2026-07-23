import { create } from 'zustand';

import type { RegisteredUser } from '../types';

type SessionState = {
  token: string | null;
  user: RegisteredUser | null;
  setSession: (token: string, user: RegisteredUser) => void;
  clear: () => void;
};

export const useSessionStore = create<SessionState>((set) => ({
  token: null,
  user: null,
  setSession: (token, user) => set({ token, user }),
  clear: () => set({ token: null, user: null }),
}));

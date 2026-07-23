import { create } from 'zustand';

type SessionState = {
  token: string | null;
  setToken: (token: string) => void;
  clear: () => void;
};

export const useSessionStore = create<SessionState>((set) => ({
  token: null,
  setToken: (token) => set({ token }),
  clear: () => set({ token: null }),
}));

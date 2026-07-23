export type RegisteredUser = {
  id: string;
  email: string;
};

export type RegisterResult =
  | { type: 'success'; user: RegisteredUser; token: string }
  | { type: 'userExists' }
  | { type: 'error'; message: string };

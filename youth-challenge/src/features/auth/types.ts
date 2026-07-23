export type RegisteredUser = {
  id: string;
  email: string;
};

export type RegisterResult =
  | { type: 'success'; user: RegisteredUser }
  | { type: 'userExists' }
  | { type: 'error'; message: string };

export type LinkTokenResult =
  | { type: 'success'; linkToken: string; linkWebUrl: string }
  | { type: 'unauthorized' }
  | { type: 'notProvisioned' }
  | { type: 'error'; message: string };

export type Device = {
  provider: string;
  status: string;
  updatedAt: string;
};

export type VitalReading = {
  provider: string;
  metric: string;
  value: number;
  unit: string | null;
  measuredAt: string;
};

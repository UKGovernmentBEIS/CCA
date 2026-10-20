import { RequestTaskPayload } from 'cca-api';

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- base intersection type for legacy form payloads; individual payloads narrow this
export type GenericRequestTaskPayload = RequestTaskPayload & Record<string, any>;

export interface FormIdentity {
  subtask?: string;
  step?: string | undefined;
}

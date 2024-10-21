import { RequestTaskPayload } from 'cca-api';

export type GenericRequestTaskPayload = RequestTaskPayload & { [key: string]: any };

export interface FormIdentity {
  subtask?: string;
  step?: string | undefined;
}

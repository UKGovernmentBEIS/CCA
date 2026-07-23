import { RequestActionDTO } from 'cca-api';

export interface RequestActionState {
  action: RequestActionDTO | null;
}

export const initialRequestActionState: RequestActionState = {
  action: null,
};

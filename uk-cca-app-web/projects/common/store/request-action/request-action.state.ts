import { RequestActionDTO } from 'cca-api';

export interface RequestActionState {
  action: RequestActionDTO;
}

export const initialRequestActionState: RequestActionState = {
  action: null,
};

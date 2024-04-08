import { RequestActionDTO } from 'cca-api';

export interface RequestActionState {
  action: RequestActionDTO;
}

export const initialState: RequestActionState = {
  action: null,
};

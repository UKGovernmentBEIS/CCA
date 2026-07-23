import { Injectable } from '@angular/core';

import { produce } from 'immer';

import { RequestActionDTO, RequestActionPayload } from 'cca-api';

import { SignalStore } from '../signal-store';
import { initialRequestActionState, RequestActionState } from './request-action.state';

@Injectable({ providedIn: 'root' })
export class RequestActionStore extends SignalStore<RequestActionState> {
  constructor() {
    super(initialRequestActionState);
  }

  setAction(action: RequestActionDTO) {
    this.setState(
      produce(this.state, (state) => {
        state.action = action;
      }),
    );
  }

  setSubmitter(submitter: string) {
    this.setState(
      produce(this.state, (state) => {
        if (!state.action) {
          throw new Error('Cannot set submitter: action is not initialized');
        }
        state.action.submitter = submitter;
      }),
    );
  }

  setType(type: RequestActionDTO['type']) {
    this.setState(
      produce(this.state, (state) => {
        if (!state.action) {
          throw new Error('Cannot set type: action is not initialized');
        }
        state.action.type = type;
      }),
    );
  }

  setPayload(payload: RequestActionPayload) {
    this.setState(
      produce(this.state, (state) => {
        if (!state.action) {
          throw new Error('Cannot set payload: action is not initialized');
        }
        state.action.payload = payload;
      }),
    );
  }
}

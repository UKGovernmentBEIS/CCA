import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaOperatorUserDetailsDTO } from 'cca-api';

type ActiveOperatorState = {
  editable: boolean;
  details: CcaOperatorUserDetailsDTO;
};

const initialState: ActiveOperatorState = {
  editable: false,
  details: null,
};
@Injectable()
export class ActiveOperatorStore extends SignalStore<ActiveOperatorState> {
  constructor() {
    super(initialState);
  }
}

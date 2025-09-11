import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { AccountBuyOutSurplusInfoDTO } from 'cca-api';

interface BuyoutAndSurplusTabState {
  surplusInfo: AccountBuyOutSurplusInfoDTO;
}

const initialState: BuyoutAndSurplusTabState = {
  surplusInfo: null,
};

@Injectable()
export class BuyoutAndSurplusTabStore extends SignalStore<BuyoutAndSurplusTabState> {
  constructor() {
    super(initialState);
  }
}

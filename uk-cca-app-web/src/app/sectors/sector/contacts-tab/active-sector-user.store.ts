import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { SectorUserAuthorityDetailsDTO } from 'cca-api';

type ActiveSectorUserStoreType = {
  editable: boolean;
  details: SectorUserAuthorityDetailsDTO;
};

const initialState: ActiveSectorUserStoreType = {
  editable: false,
  details: null,
};

@Service({ autoProvided: false })
export class ActiveSectorUserStore extends SignalStore<ActiveSectorUserStoreType> {
  constructor() {
    super(initialState);
  }
}

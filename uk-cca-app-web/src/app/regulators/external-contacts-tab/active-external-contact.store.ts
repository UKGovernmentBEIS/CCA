import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CaExternalContactDTO } from 'cca-api';

@Service({ autoProvided: false })
export class ActiveExternalContactStore extends SignalStore<CaExternalContactDTO> {
  constructor() {
    super(null);
  }
}

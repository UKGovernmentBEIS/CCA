import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CaExternalContactDTO } from 'cca-api';

@Injectable()
export class ActiveExternalContactStore extends SignalStore<CaExternalContactDTO> {
  constructor() {
    super(null);
  }
}

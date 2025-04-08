import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { MiReportSearchResult } from 'cca-api';

@Injectable({ providedIn: 'root' })
export class MiReportsStore extends SignalStore<MiReportSearchResult[]> {
  constructor() {
    super(null);
  }
}

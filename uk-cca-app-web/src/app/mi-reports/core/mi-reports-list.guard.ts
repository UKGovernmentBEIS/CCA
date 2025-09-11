import { inject } from '@angular/core';

import { map, tap } from 'rxjs';

import { MiReportsService } from 'cca-api';

import { MiReportsStore } from '../mi-reports.store';

export const miReportsListGuard = () => {
  const store = inject(MiReportsStore);

  return inject(MiReportsService)
    .getCurrentUserMiReports()
    .pipe(
      tap((result) => store.setState(result)),
      map(() => true),
    );
};

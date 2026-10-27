import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { SubsistenceFeesRunDetailsDTO } from 'cca-api';

import { TuMoaDetailsStore } from './tu-moa-details/tu-moa-details.store';

export const TU_MOAS_ROUTES: Routes = [
  {
    path: ':moaId',
    data: {
      breadcrumb: ({ subFeesDetails }: { subFeesDetails: SubsistenceFeesRunDetailsDTO }) => ({
        text: `${subFeesDetails.paymentRequestId}: Target unit MoAs`,
        from: ':runId',
        fragment: 'tu-moas',
      }),
    },
    children: [
      {
        path: 'details',
        canActivate: [
          () => {
            inject(TuMoaDetailsStore).initialize();
            return true;
          },
        ],
        canDeactivate: [
          () => {
            inject(TuMoaDetailsStore).reset();
            return true;
          },
        ],
        loadChildren: () =>
          import('./tu-moa-details/tu-moa-details.routes').then((r) => r.TARGET_UNIT_MOA_DETAILS_ROUTES),
        providers: [TuMoaDetailsStore],
      },
      {
        title: 'Your download has started',
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
      },
    ],
  },
];

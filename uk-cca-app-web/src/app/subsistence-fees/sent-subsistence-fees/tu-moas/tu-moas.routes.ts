import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { TuMoaDetailsStore } from './tu-moa-details/tu-moa-details.store';

export const TU_MOAS_ROUTES: Routes = [
  {
    path: ':moaId',
    data: {
      breadcrumb: ({ subFeesDetails }) => ({
        text: `${subFeesDetails.paymentRequestId}: Target unit MoAs`,
        link: `/subsistence-fees/sent-subsistence-fees/${subFeesDetails.runId}`,
        fragment: 'tu-moas',
      }),
    },
    children: [
      {
        path: 'details',
        providers: [TuMoaDetailsStore],
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
      },
      {
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
    ],
  },
];

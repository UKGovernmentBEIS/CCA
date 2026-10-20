import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { SectorMoaDetailsStore } from '@shared/components';

import { SectorMoasDetailsResolver } from './sector-moas.resolver';

export const SECTOR_MOAS_ROUTES: Routes = [
  {
    path: ':moaId',
    data: {
      breadcrumb: ({ subFeesDetails }) => ({
        text: `${subFeesDetails.paymentRequestId}: Sector MoAs`,
        link: `/subsistence-fees/sent-subsistence-fees/${subFeesDetails.runId}`,
        fragment: 'sector-moas',
      }),
    },
    children: [
      {
        path: 'details',
        providers: [SectorMoaDetailsStore],
        canActivate: [
          () => {
            inject(SectorMoaDetailsStore).initialize();
            return true;
          },
        ],
        canDeactivate: [
          () => {
            inject(SectorMoaDetailsStore).reset();
            return true;
          },
        ],
        resolve: { sectorMoaDetails: SectorMoasDetailsResolver },
        data: {
          breadcrumb: ({ sectorMoaDetails }) => `${sectorMoaDetails.transactionId}`,
        },
        loadChildren: () => import('@shared/components').then((r) => r.SECTOR_MOA_DETAILS_ROUTES),
      },
      {
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
    ],
  },
];

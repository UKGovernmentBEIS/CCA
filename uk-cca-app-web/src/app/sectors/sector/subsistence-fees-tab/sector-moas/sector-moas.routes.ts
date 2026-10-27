import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { SectorMoaDetailsStore } from '@shared/components';

import { SubsistenceFeesMoaDetailsDTO } from 'cca-api';

import { SectorMoasDetailsResolver } from './sector-moas.resolver';

export const SECTOR_MOAS_ROUTES: Routes = [
  {
    path: ':moaId',
    children: [
      {
        path: 'details',
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
        data: {
          breadcrumb: ({ sectorMoaDetails }: { sectorMoaDetails: SubsistenceFeesMoaDetailsDTO }) =>
            `${sectorMoaDetails.transactionId}`,
        },
        resolve: { sectorMoaDetails: SectorMoasDetailsResolver },
        loadChildren: () => import('@shared/components').then((r) => r.SECTOR_MOA_DETAILS_ROUTES),
        providers: [SectorMoaDetailsStore],
      },
      {
        title: 'Your download has started',
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
      },
    ],
  },
];

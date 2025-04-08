import { Routes } from '@angular/router';

import { SectorMoasDetailsResolver } from './sector-moas.resolver';

export const SECTOR_MOAS_ROUTES: Routes = [
  {
    path: ':moaId',
    children: [
      {
        path: 'details',
        resolve: { sectorMoaDetails: SectorMoasDetailsResolver },
        loadChildren: () =>
          import('./sector-moa-details/sector-moa-details.routes').then((r) => r.SECTOR_MOA_DETAILS_ROUTES),
      },
      {
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
    ],
  },
];

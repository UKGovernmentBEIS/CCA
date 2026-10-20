import { Routes } from '@angular/router';

import { SentSubsistenceFeesDetailsResolver } from './sent-subsistence-fees.resolver';

export const SENT_SUBSISTENCE_FEES_ROUTES: Routes = [
  {
    path: ':runId',
    resolve: { subFeesDetails: SentSubsistenceFeesDetailsResolver },
    data: {
      breadcrumb: {
        text: 'Subsistence fees',
        fragment: 'sent-subsistence-fees',
        link: '/subsistence-fees',
      },
    },
    children: [
      {
        path: '',
        loadComponent: () => import('./sent-subsistence-fees.component').then((c) => c.SentSubsistenceFeesComponent),
      },
      {
        path: 'sector-moas',
        loadChildren: () => import('./sector-moas/sector-moas.routes').then((r) => r.SECTOR_MOAS_ROUTES),
      },
      {
        path: 'tu-moas',
        loadChildren: () => import('./tu-moas/tu-moas.routes').then((r) => r.TU_MOAS_ROUTES),
      },
    ],
  },
];

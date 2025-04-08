import { Routes } from '@angular/router';

export const TU_MOAS_ROUTES: Routes = [
  {
    path: ':moaId',
    children: [
      {
        path: 'details',
        loadComponent: () => import('./tu-moa-details/tu-moa-details.component').then((c) => c.TuMoaDetailsComponent),
      },
      {
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
    ],
  },
];

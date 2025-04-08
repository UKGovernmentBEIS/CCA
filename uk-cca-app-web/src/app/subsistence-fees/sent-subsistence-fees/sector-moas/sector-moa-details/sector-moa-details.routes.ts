import { Routes } from '@angular/router';

export const SECTOR_MOA_DETAILS_ROUTES: Routes = [
  {
    path: '',
    data: { breadcrumb: 'Sector MoA Details' },
    loadComponent: () => import('./sector-moa-details.component').then((c) => c.SectorMoaDetailsComponent),
  },
  {
    path: ':moaTUId/tu-details',
    loadComponent: () =>
      import('./sector-moa-tu-details/sector-moa-tu-details.component').then((c) => c.SectorMoaTuDetailsComponent),
  },
];

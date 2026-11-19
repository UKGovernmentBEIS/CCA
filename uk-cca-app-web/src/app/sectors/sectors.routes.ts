import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { SectorGuard } from './sector.guard';
import { ActiveSectorStore } from './sector/active-sector.store';
import { SECTOR_ROUTES } from './sector/sector.routes';
import { sectorBreadcrumb } from './sector/sector-breadcrumb';

export const SECTORS_ROUTES: Routes = [
  {
    title: 'Sectors',
    path: '',
    loadComponent: () => import('./sectors-list/sector-list.component').then((c) => c.SectorListComponent),
    canDeactivate: [PendingRequestGuard],
  },
  {
    title: 'Sector details',
    path: ':sectorId',
    canActivate: [SectorGuard],
    data: { breadcrumb: sectorBreadcrumb },
    resolve: { details: () => inject(ActiveSectorStore).state },
    children: SECTOR_ROUTES,
    providers: [ActiveSectorStore],
  },
];

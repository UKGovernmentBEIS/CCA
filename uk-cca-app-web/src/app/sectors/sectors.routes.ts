import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { SectorGuard } from './sector.guard';
import { ActiveSectorStore } from './sector/active-sector.store';
import { SECTOR_ROUTES } from './sector/sector.routes';
import { SectorListComponent } from './sectors-list/sector-list.component';

export const SECTORS_ROUTES: Routes = [
  {
    path: '',
    data: { pageTitle: 'Manage Sectors' },
    component: SectorListComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':sectorId',
    providers: [ActiveSectorStore],
    canActivate: [SectorGuard],
    data: {
      pageTitle: 'Sector details',
      breadcrumb: {
        resolveText: ({ details }) =>
          `${details.sectorAssociationDetails.acronym} - ${details.sectorAssociationDetails.commonName}`,
      },
    },
    resolve: { details: () => inject(ActiveSectorStore).state },
    children: SECTOR_ROUTES,
  },
];

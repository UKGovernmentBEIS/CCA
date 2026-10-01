import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { SectorAssociationResponseDTO } from 'cca-api';

import { SectorGuard } from './sector.guard';
import { ActiveSectorStore } from './sector/active-sector.store';
import { SECTOR_ROUTES } from './sector/sector.routes';
import { SectorListComponent } from './sectors-list/sector-list.component';

export const SECTORS_ROUTES: Routes = [
  {
    path: '',
    title: 'Sectors',
    component: SectorListComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':sectorId',
    title: 'Sector details',
    providers: [ActiveSectorStore],
    canActivate: [SectorGuard],
    data: {
      breadcrumb: ({ details }: { details: SectorAssociationResponseDTO }) =>
        `${details.sectorAssociationDetails.acronym} - ${details.sectorAssociationDetails.commonName}`,
    },
    resolve: { details: () => inject(ActiveSectorStore).state },
    children: SECTOR_ROUTES,
  },
];

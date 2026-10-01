import { Routes } from '@angular/router';

import { SubsectorAssociationSchemesDTO } from 'cca-api';

import { canEditAdvancedDetailsGuard } from './can-edit-advanced-details.guard';
import { SectorAssociationSchemeResolver } from './sector-scheme.resolver';
import { SubsectorAssociationSchemeResolver } from './subsector-scheme.resolver';

export const SCHEME_ROUTES: Routes = [
  {
    path: 'subsector/:subId',
    title: 'Sub-sector details',
    data: {
      breadcrumb: ({ subSector }: { subSector: SubsectorAssociationSchemesDTO }) => `${subSector.name}`,
    },
    resolve: { subSector: SubsectorAssociationSchemeResolver },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: '',
        title: 'Sub-sector details',
        loadComponent: () =>
          import('./sub-sector-details/sub-sector-details.component').then((c) => c.SubSectorDetailsComponent),
      },
      {
        path: 'sector-commitment',
        title: 'CCA3 sector commitment',
        data: { breadcrumb: false, backlink: '..' },
        loadComponent: () =>
          import('./sector-commitment/sector-commitment.component').then((c) => c.SectorCommitmentComponent),
      },
    ],
  },
  {
    path: 'sector-documents/:uuid',
    title: 'Your download has started',
    loadComponent: () =>
      import('./sector-documents-download/sector-documents-download.component').then(
        (c) => c.SectorDocumentsDownloadComponent,
      ),
  },
  {
    path: 'umbrella-agreement',
    title: 'Umbrella agreement CCA3',
    data: { breadcrumb: false, backlink: '..' },
    canActivate: [canEditAdvancedDetailsGuard],
    resolve: { sectorScheme: SectorAssociationSchemeResolver },
    loadComponent: () =>
      import('./umbrella-agreement/umbrella-agreement.component').then((c) => c.UmbrellaAgreementComponent),
  },
  {
    path: 'sector-commitment',
    title: 'CCA3 sector commitment',
    data: { breadcrumb: false, backlink: '..' },
    canActivate: [canEditAdvancedDetailsGuard],
    resolve: { sectorScheme: SectorAssociationSchemeResolver },
    loadComponent: () =>
      import('./sector-commitment/sector-commitment.component').then((c) => c.SectorCommitmentComponent),
  },
];

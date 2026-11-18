import { Routes } from '@angular/router';

import { canEditAdvancedDetailsGuard } from './can-edit-advanced-details.guard';
import { SectorAssociationSchemeResolver } from './sector-scheme.resolver';
import { SubsectorAssociationSchemeResolver } from './subsector-scheme.resolver';

export const SCHEME_ROUTES: Routes = [
  {
    path: 'subsector/:subId',
    data: {
      pageTitle: 'Sub-sector details',
      breadcrumb: ({ subSector }) => `${subSector.name}`,
    },
    resolve: { subSector: SubsectorAssociationSchemeResolver },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./sub-sector-details/sub-sector-details.component').then((c) => c.SubSectorDetailsComponent),
      },
      {
        path: 'sector-commitment',
        data: { breadcrumb: false, backlink: '..' },
        loadComponent: () =>
          import('./sector-commitment/sector-commitment.component').then((c) => c.SectorCommitmentComponent),
      },
    ],
  },
  {
    path: 'sector-documents/:uuid',
    loadComponent: () =>
      import('./sector-documents-download/sector-documents-download.component').then(
        (c) => c.SectorDocumentsDownloadComponent,
      ),
  },
  {
    path: 'umbrella-agreement',
    data: { breadcrumb: false, backlink: '..' },
    canActivate: [canEditAdvancedDetailsGuard],
    resolve: { sectorScheme: SectorAssociationSchemeResolver },
    loadComponent: () =>
      import('./umbrella-agreement/umbrella-agreement.component').then((c) => c.UmbrellaAgreementComponent),
  },
  {
    path: 'sector-commitment',
    data: { breadcrumb: false, backlink: '..' },
    canActivate: [canEditAdvancedDetailsGuard],
    resolve: { sectorScheme: SectorAssociationSchemeResolver },
    loadComponent: () =>
      import('./sector-commitment/sector-commitment.component').then((c) => c.SectorCommitmentComponent),
  },
];

import { Routes } from '@angular/router';

import { SubsectorAssociationSchemesDTO } from 'cca-api';

import { sectorTabBreadcrumb } from '../sector-breadcrumb';
import { canEditAdvancedDetailsGuard } from './can-edit-advanced-details.guard';
import { SectorAssociationSchemeResolver } from './sector-scheme.resolver';
import { SubsectorAssociationSchemeResolver } from './subsector-scheme.resolver';

export const SCHEME_ROUTES: Routes = [
  {
    path: 'subsector',
    data: { breadcrumb: sectorTabBreadcrumb('scheme') },
    children: [
      {
        title: 'Sub-sector details',
        path: ':subId',
        data: {
          breadcrumb: ({ subSector }: { subSector: SubsectorAssociationSchemesDTO }) => `${subSector.name}`,
        },
        resolve: { subSector: SubsectorAssociationSchemeResolver },
        children: [
          {
            title: 'Sub-sector details',
            path: '',
            loadComponent: () =>
              import('./sub-sector-details/sub-sector-details.component').then((c) => c.SubSectorDetailsComponent),
          },
          {
            title: 'CCA3 sector commitment',
            path: 'sector-commitment',
            loadComponent: () =>
              import('./sector-commitment/sector-commitment.component').then((c) => c.SectorCommitmentComponent),
            data: { breadcrumb: false, backlink: '..' },
          },
        ],
        runGuardsAndResolvers: 'always',
      },
    ],
  },
  {
    title: 'Your download has started',
    path: 'sector-documents/:uuid',
    loadComponent: () =>
      import('./sector-documents-download/sector-documents-download.component').then(
        (c) => c.SectorDocumentsDownloadComponent,
      ),
  },
  {
    title: 'Umbrella agreement CCA3',
    path: 'umbrella-agreement',
    loadComponent: () =>
      import('./umbrella-agreement/umbrella-agreement.component').then((c) => c.UmbrellaAgreementComponent),
    canActivate: [canEditAdvancedDetailsGuard],
    data: { breadcrumb: false, backlink: { link: '..', fragment: 'scheme' } },
    resolve: { sectorScheme: SectorAssociationSchemeResolver },
  },
  {
    title: 'CCA3 sector commitment',
    path: 'sector-commitment',
    loadComponent: () =>
      import('./sector-commitment/sector-commitment.component').then((c) => c.SectorCommitmentComponent),
    canActivate: [canEditAdvancedDetailsGuard],
    data: { breadcrumb: false, backlink: { link: '..', fragment: 'scheme' } },
    resolve: { sectorScheme: SectorAssociationSchemeResolver },
  },
];

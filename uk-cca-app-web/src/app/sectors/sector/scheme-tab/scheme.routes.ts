import { Routes } from '@angular/router';

import { SubsectorAssociationSchemeResolver } from './subsector-scheme.resolver';

export const SCHEME_ROUTES: Routes = [
  {
    path: 'subsector/:subId',
    data: {
      pageTitle: 'Sub-sector details',
      breadcrumb: ({ subSector }) => `${subSector.name}`,
    },
    resolve: { subSector: SubsectorAssociationSchemeResolver },
    loadComponent: () =>
      import('./sub-sector-details/sub-sector-details.component').then((c) => c.SubSectorDetailsComponent),
  },
  {
    path: 'sector-documents/:uuid',
    loadComponent: () =>
      import('./sector-documents-download/sector-documents-download.component').then(
        (c) => c.SectorDocumentsDownloadComponent,
      ),
  },
];

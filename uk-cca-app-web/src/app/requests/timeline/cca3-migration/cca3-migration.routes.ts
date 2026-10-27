import { Routes } from '@angular/router';

export const CCA3_MIGRATION_ROUTES: Routes = [
  {
    path: 'facility',
    children: [
      {
        title: 'Facility',
        path: ':facilityId',
        loadComponent: () =>
          import('./migrated-facility-details/migrated-facility-details.component').then(
            (c) => c.MigratedFacilityDetailsComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];

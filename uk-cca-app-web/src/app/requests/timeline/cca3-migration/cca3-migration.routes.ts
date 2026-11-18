import { Routes } from '@angular/router';

export const CCA3_MIGRATION_ROUTES: Routes = [
  {
    path: 'facility',
    children: [
      {
        path: ':facilityId',
        title: 'Facility',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./migrated-facility-details/migrated-facility-details.component').then(
            (c) => c.MigratedFacilityDetailsComponent,
          ),
      },
    ],
  },
];

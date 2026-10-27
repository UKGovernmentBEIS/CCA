import { Routes } from '@angular/router';

export const MANAGE_FACILITIES_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Manage facilities',
        path: '',
        pathMatch: 'full',
        loadComponent: () => import('./manage-facilities.component').then((c) => c.ManageFacilitiesComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: ':facilityId',
        loadChildren: () => import('./facility/facility.routes').then((r) => r.FACILITY_ROUTES),
      },
    ],
  },
];

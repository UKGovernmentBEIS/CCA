import { Routes } from '@angular/router';

export const MANAGE_FACILITIES_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        data: { backlink: '../../', breadcrumb: false },
        title: 'Manage facilities',
        loadComponent: () => import('./manage-facilities.component').then((m) => m.ManageFacilitiesComponent),
      },
      {
        path: ':facilityId',
        loadChildren: () => import('./facility/facility.routes').then((m) => m.FACILITY_ROUTES),
      },
    ],
  },
];

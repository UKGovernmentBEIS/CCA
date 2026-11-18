import { Routes } from '@angular/router';

import { ManageFacilitiesWizardStep } from '@requests/common';

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
        path: ManageFacilitiesWizardStep.ADD_FACILITY,
        title: 'Add facility',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('./add/add-facility.component').then((m) => m.AddFacilityComponent),
      },
      {
        path: ':facilityId',
        loadChildren: () => import('./facility/facility.routes').then((m) => m.FACILITY_ROUTES),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.DELETE_FACILITY}`,
        title: 'Delete facility',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./delete/delete-facility-item.component').then((m) => m.DeleteFacilityItemComponent),
      },
    ],
  },
];

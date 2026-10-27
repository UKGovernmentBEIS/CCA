import { Routes } from '@angular/router';

import { ManageFacilitiesWizardStep } from '@requests/common';

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
        title: 'Add facility',
        path: ManageFacilitiesWizardStep.ADD_FACILITY,
        loadComponent: () => import('./add/add-facility.component').then((c) => c.AddFacilityComponent),
        data: { backlink: '../', breadcrumb: false },
      },
      {
        path: ':facilityId',
        loadChildren: () => import('./facility/facility.routes').then((r) => r.FACILITY_ROUTES),
      },
      {
        title: 'Exclude facility',
        path: `:facilityId/${ManageFacilitiesWizardStep.EXCLUDE_FACILITY}`,
        loadComponent: () =>
          import('./exclude/facility-item-exclude.component').then((c) => c.FacilityItemExcludeComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Undo facility',
        path: `:facilityId/${ManageFacilitiesWizardStep.UNDO_FACILITY}`,
        loadComponent: () => import('./undo/facility-item-undo.component').then((c) => c.FacilityItemUndoComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Delete facility',
        path: `:facilityId/${ManageFacilitiesWizardStep.DELETE_FACILITY}`,
        loadComponent: () =>
          import('./delete/facility-item-delete.component').then((c) => c.FacilityItemDeleteComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
    ],
  },
];

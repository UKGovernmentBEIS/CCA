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
        loadComponent: () => import('./manage-facilities.component').then((c) => c.ManageFacilitiesComponent),
      },
      {
        path: ManageFacilitiesWizardStep.ADD_FACILITY,
        title: 'Add facility',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('./add/add-facility.component').then((c) => c.AddFacilityComponent),
      },
      {
        path: ':facilityId',
        loadChildren: () => import('./facility/facility.routes').then((m) => m.FACILITY_ROUTES),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.EXCLUDE_FACILITY}`,
        title: 'Exclude facility',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./exclude/facility-item-exclude.component').then((c) => c.FacilityItemExcludeComponent),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.UNDO_FACILITY}`,
        title: 'Undo facility',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./undo/facility-item-undo.component').then((c) => c.FacilityItemUndoComponent),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.DELETE_FACILITY}`,
        title: 'Delete facility',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./delete/facility-item-delete.component').then((c) => c.FacilityItemDeleteComponent),
      },
    ],
  },
];

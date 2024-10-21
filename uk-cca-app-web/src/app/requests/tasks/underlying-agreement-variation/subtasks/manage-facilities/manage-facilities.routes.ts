import { Routes } from '@angular/router';

import { canActivateFacilityItem, ManageFacilitiesWizardStep } from '@requests/common';

export const MANAGE_FACILITIES_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: ManageFacilitiesWizardStep.SUMMARY,
        data: { backlink: '../../../', breadcrumb: false },
        title: 'Manage facilities list',
        loadComponent: () =>
          import('./summary/manage-facilities-summary.component').then((c) => c.ManageFacilitiesSummaryComponent),
      },
      {
        path: ManageFacilitiesWizardStep.ADD_FACILITY,
        title: 'Add facility',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.FacilityItemComponent),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.EDIT_FACILITY}`,
        title: 'Edit facility',
        canActivate: [canActivateFacilityItem],
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.FacilityItemComponent),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.EXCLUDE_FACILITY}`,
        title: 'Exclude facility',
        canActivate: [canActivateFacilityItem],
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./exclude/facility-item-exclude.component').then((c) => c.FacilityItemExcludeComponent),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.UNDO_FACILITY}`,
        title: 'Undo facility',
        canActivate: [canActivateFacilityItem],
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./undo/facility-item-undo.component').then((c) => c.FacilityItemUndoComponent),
      },
      {
        path: `:facilityId/${ManageFacilitiesWizardStep.DELETE_FACILITY}`,
        title: 'Delete facility',
        canActivate: [canActivateFacilityItem],
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.DeleteFacilityItemComponent),
      },
      {
        path: '**',
        redirectTo: ManageFacilitiesWizardStep.SUMMARY,
      },
    ],
  },
];

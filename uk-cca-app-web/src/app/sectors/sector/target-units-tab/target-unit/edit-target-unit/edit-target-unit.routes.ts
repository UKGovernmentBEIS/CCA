import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { EditTargetUnitSubSectorResolver } from './edit-target-unit-subsector.resolver';

export const EDIT_TARGET_UNIT_ROUTES: Routes = [
  {
    path: 'edit',
    children: [
      {
        title: 'Edit target unit details',
        path: 'details',
        loadComponent: () => import('./edit-details/edit-details.component').then((c) => c.EditDetailsComponent),
        canDeactivate: [PendingRequestGuard],
        data: {
          backlink: '../../',
          breadcrumb: false,
        },
        resolve: { subSectorScheme: EditTargetUnitSubSectorResolver },
      },
      {
        title: 'Edit financial independence',
        path: 'financial-independence',
        loadComponent: () =>
          import('./edit-financial-independence/edit-financial-independence.component').then(
            (c) => c.EditFinancialIndependenceComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Edit responsible person details',
        path: 'responsible-person',
        loadComponent: () =>
          import('./edit-responsible-person/edit-responsible-person.component').then(
            (c) => c.EditResponsiblePersonComponent,
          ),
        canDeactivate: [PendingRequestGuard],
        data: {
          backlink: '../../',
          breadcrumb: false,
        },
      },
      {
        title: 'Edit administrative contact details',
        path: 'administrative-contact',
        loadComponent: () =>
          import('./edit-administrative-contact/edit-administrative-contact.component').then(
            (c) => c.EditAdministrativeContactComponent,
          ),
        canDeactivate: [PendingRequestGuard],
        data: {
          backlink: '../../',
          breadcrumb: false,
        },
      },
    ],
  },
];

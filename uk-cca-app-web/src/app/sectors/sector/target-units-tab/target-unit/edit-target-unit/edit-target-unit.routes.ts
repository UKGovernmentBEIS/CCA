import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { EditTargetUnitSubSectorResolver } from './edit-target-unit-subsector.resolver';

export const EDIT_TARGET_UNIT_ROUTES: Routes = [
  {
    path: 'edit',
    children: [
      {
        path: 'details',
        title: 'Edit target unit details',
        resolve: { subSectorScheme: EditTargetUnitSubSectorResolver },
        data: {
          backlink: '../../',
          breadcrumb: false,
        },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('./edit-details/edit-details.component').then((c) => c.EditDetailsComponent),
      },
      {
        path: 'financial-independence',
        title: 'Edit financial independence',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./edit-financial-independence/edit-financial-independence.component').then(
            (c) => c.EditFinancialIndependenceComponent,
          ),
      },
      {
        path: 'responsible-person',
        title: 'Edit responsible person details',
        data: {
          backlink: '../../',
          breadcrumb: false,
        },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./edit-responsible-person/edit-responsible-person.component').then(
            (c) => c.EditResponsiblePersonComponent,
          ),
      },
      {
        path: 'administrative-contact',
        title: 'Edit administrative contact details',
        data: {
          backlink: '../../',
          breadcrumb: false,
        },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./edit-administrative-contact/edit-administrative-contact.component').then(
            (c) => c.EditAdministrativeContactComponent,
          ),
      },
    ],
  },
];

import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { EditTargetUnitSubSectorResolver } from './edit-target-unit-subsector.resolver';

export const EDIT_TARGET_UNIT_ROUTES: Routes = [
  {
    path: 'edit',
    children: [
      {
        path: 'details',
        resolve: { subSectorScheme: EditTargetUnitSubSectorResolver },
        data: {
          pageTitle: 'Edit target unit details',
          backlink: '../../',
          breadcrumb: false,
        },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('./edit-details/edit-details.component').then((c) => c.EditDetailsComponent),
      },
      {
        path: 'financial-independence',
        data: { backlink: '../../', breadcrumb: false, pageTitle: 'Edit financial independence' },
        loadComponent: () =>
          import('./edit-financial-independence/edit-financial-independence.component').then(
            (c) => c.EditFinancialIndependenceComponent,
          ),
      },
      {
        path: 'responsible-person',
        data: {
          pageTitle: 'Edit responsible person details',
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
        data: {
          pageTitle: 'Edit administrative contact details',
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

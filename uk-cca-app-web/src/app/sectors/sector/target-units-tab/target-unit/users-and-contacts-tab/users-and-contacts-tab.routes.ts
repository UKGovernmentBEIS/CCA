import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { CanEditOperatorUserDetailsGuard } from './operator-details.guard';
import { ActiveOperatorStore } from './operator-details/active-operator.store';

export const USERS_AND_CONTACTS_ROUTES: Routes = [
  {
    path: 'users',
    children: [
      {
        path: 'add',
        loadComponent: () => import('./add/add-operator.component').then((c) => c.AddOperatorComponent),
      },
      {
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/confirmation.component').then((c) => c.AddOperatorConfirmationComponent),
      },
      {
        path: ':userId',
        providers: [ActiveOperatorStore],
        canActivate: [CanEditOperatorUserDetailsGuard],
        resolve: { operatorDetails: () => inject(ActiveOperatorStore).state.details },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Operator details',
              breadcrumb: {
                resolveText: ({ operatorDetails }) => `${operatorDetails.firstName} ${operatorDetails.lastName}`,
              },
            },
            loadComponent: () =>
              import('./operator-details/operator-details.component').then((c) => c.OperatorDetailsComponent),
          },
          {
            path: 'edit',
            data: {
              pageTitle: 'Edit operator details',
              breadcrumb: false,
            },
            loadComponent: () =>
              import('./operator-details/edit/edit-operator-details.component').then(
                (c) => c.EditOperatorDetailsComponent,
              ),
          },
          {
            path: 'delete',
            data: {
              pageTitle: 'Delete operator',
              breadcrumb: false,
              backlink: '../../../',
            },
            loadComponent: () =>
              import('./operator-details/delete/delete-operator.component').then((c) => c.DeleteOperatorComponent),
          },
        ],
      },
    ],
  },
];

import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { CcaOperatorUserDetailsDTO } from 'cca-api';

import { CanEditOperatorUserDetailsGuard } from './operator-details.guard';
import { ActiveOperatorStore } from './operator-details/active-operator.store';

export const USERS_AND_CONTACTS_ROUTES: Routes = [
  {
    path: 'users',
    children: [
      {
        path: 'add',
        title: 'Add an operator user',
        loadComponent: () => import('./add/add-operator.component').then((c) => c.AddOperatorComponent),
      },
      {
        path: 'confirmation',
        title: 'Operator user added',
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
            title: 'Operator details',
            data: {
              breadcrumb: ({ operatorDetails }: { operatorDetails: CcaOperatorUserDetailsDTO }) =>
                `${operatorDetails.firstName} ${operatorDetails.lastName}`,
            },
            loadComponent: () =>
              import('./operator-details/operator-details.component').then((c) => c.OperatorDetailsComponent),
          },
          {
            path: 'edit',
            title: 'Edit operator details',
            data: {
              breadcrumb: false,
            },
            loadComponent: () =>
              import('./operator-details/edit/edit-operator-details.component').then(
                (c) => c.EditOperatorDetailsComponent,
              ),
          },
          {
            path: 'delete',
            title: 'Delete operator',
            data: {
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

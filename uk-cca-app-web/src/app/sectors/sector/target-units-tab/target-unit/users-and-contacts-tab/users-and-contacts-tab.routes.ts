import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { CcaOperatorUserDetailsDTO } from 'cca-api';

import { targetUnitTabBreadcrumb } from '../../target-unit-breadcrumb';
import { CanEditOperatorUserDetailsGuard } from './operator-details.guard';
import { ActiveOperatorStore } from './operator-details/active-operator.store';

export const USERS_AND_CONTACTS_ROUTES: Routes = [
  {
    path: 'users',
    data: { breadcrumb: targetUnitTabBreadcrumb('users-and-contacts') },
    children: [
      {
        title: 'Add an operator user',
        path: 'add',
        loadComponent: () => import('./add/add-operator.component').then((c) => c.AddOperatorComponent),
      },
      {
        title: 'Operator user added',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/confirmation.component').then((c) => c.AddOperatorConfirmationComponent),
      },
      {
        path: ':userId',
        canActivate: [CanEditOperatorUserDetailsGuard],
        resolve: { operatorDetails: () => inject(ActiveOperatorStore).state.details },
        children: [
          {
            title: 'Operator details',
            path: '',
            loadComponent: () =>
              import('./operator-details/operator-details.component').then((c) => c.OperatorDetailsComponent),
            data: {
              breadcrumb: ({ operatorDetails }: { operatorDetails: CcaOperatorUserDetailsDTO }) =>
                `${operatorDetails.firstName} ${operatorDetails.lastName}`,
            },
          },
          {
            title: 'Edit operator details',
            path: 'edit',
            loadComponent: () =>
              import('./operator-details/edit/edit-operator-details.component').then(
                (c) => c.EditOperatorDetailsComponent,
              ),
            data: {
              breadcrumb: false,
              backlink: '../',
            },
          },
          {
            title: 'Delete operator',
            path: 'delete',
            loadComponent: () =>
              import('./operator-details/delete/delete-operator.component').then((c) => c.DeleteOperatorComponent),
            data: {
              breadcrumb: false,
              backlink: { link: '../../../', fragment: 'users-and-contacts' },
            },
          },
        ],
        providers: [ActiveOperatorStore],
      },
    ],
  },
];

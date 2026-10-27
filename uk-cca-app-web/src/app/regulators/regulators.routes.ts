import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { ActiveExternalContactStore } from './external-contacts-tab/active-external-contact.store';
import { ExternalContactDetailsGuard } from './external-contacts-tab/details/details.guard';
import { CanAddUsers } from './regulators-users-tab/details/can-add-users.guard';
import { CanEditUserGuard, ResetRegulatorDetails } from './regulators-users-tab/details/can-edit-user.guard';
import { DetailsStore } from './regulators-users-tab/details/details.store';

export const REGULATOR_ROUTES: Routes = [
  {
    title: 'Regulator users',
    path: '',
    loadComponent: () => import('./regulators.component').then((c) => c.RegulatorsComponent),
    canDeactivate: [PendingRequestGuard],
  },
  {
    title: 'Account confirmation email sent',
    path: 'add-confirmation',
    loadComponent: () =>
      import('./regulators-users-tab/add-confirmation/add-confirmation.component').then(
        (c) => c.AddConfirmationComponent,
      ),
  },
  {
    title: 'Add a new user',
    path: 'add',
    loadComponent: () => import('./regulators-users-tab/details/details.component').then((c) => c.DetailsComponent),
    canActivate: [CanAddUsers],
    canDeactivate: [PendingRequestGuard, ResetRegulatorDetails],
    data: { breadcrumb: false, backlink: '../' },
    providers: [DetailsStore],
  },
  {
    path: ':userId',
    canActivate: [CanEditUserGuard],
    canDeactivate: [ResetRegulatorDetails],
    resolve: { user: () => inject(DetailsStore).state.user },
    children: [
      {
        title: 'User details',
        path: '',
        pathMatch: 'full',
        loadComponent: () => import('./regulators-users-tab/details/details.component').then((c) => c.DetailsComponent),
        canDeactivate: [PendingRequestGuard],
        data: {
          breadcrumb: false,
          backlink: '../',
        },
      },
      {
        title: 'Confirm that this user account will be deleted',
        path: 'delete',
        loadComponent: () => import('./external-contacts-tab/delete/delete.component').then((c) => c.DeleteComponent),
        canDeactivate: [PendingRequestGuard],
        data: {
          backlink: '../..',
          breadcrumb: false,
        },
      },
      {
        path: '2fa',
        loadChildren: () => import('../two-fa/two-fa.routes').then((r) => r.TWO_FA_ROUTES),
      },
      {
        title: 'Your download has started',
        path: 'file-download/:uuid',
        loadComponent: () =>
          import('./regulators-users-tab/file-download/signature-file-download.component').then(
            (c) => c.SignatureFileDownloadComponent,
          ),
      },
    ],
    providers: [DetailsStore],
  },
  {
    title: 'Your download has started',
    path: 'file-download/:uuid',
    loadComponent: () =>
      import('./regulators-users-tab/file-download/signature-file-download.component').then(
        (c) => c.SignatureFileDownloadComponent,
      ),
  },
  {
    path: 'external-contacts',
    children: [
      {
        title: 'Add an external contact',
        path: 'add',
        loadComponent: () =>
          import('./external-contacts-tab/details/details.component').then((c) => c.ExternalContactsDetailsComponent),
        canDeactivate: [PendingRequestGuard],
        data: { breadcrumb: false, backlink: { link: '../..', fragment: 'external-contacts' } },
      },
      {
        path: ':userId',
        canActivate: [ExternalContactDetailsGuard],
        canDeactivate: [() => inject(ActiveExternalContactStore).reset()],
        children: [
          {
            title: 'External contact details',
            path: '',
            pathMatch: 'full',
            loadComponent: () =>
              import('./external-contacts-tab/details/details.component').then(
                (c) => c.ExternalContactsDetailsComponent,
              ),
            canDeactivate: [PendingRequestGuard],
            data: {
              breadcrumb: false,
              backlink: { link: '../..', fragment: 'external-contacts' },
            },
          },
          {
            title: 'Confirm that this external contact will be deleted',
            path: 'delete',
            loadComponent: () =>
              import('./external-contacts-tab/delete/delete.component').then((c) => c.DeleteComponent),
            canDeactivate: [PendingRequestGuard],
            data: {
              breadcrumb: false,
              backlink: { link: '../../..', fragment: 'external-contacts' },
            },
          },
        ],
      },
    ],
    providers: [ActiveExternalContactStore],
  },
  {
    title: 'Site contacts',
    path: 'site-contacts',
    loadComponent: () => import('./site-contacts-tab/site-contacts.component').then((c) => c.SiteContactsComponent),
  },
];

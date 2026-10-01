import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { ActiveExternalContactStore } from './external-contacts-tab/active-external-contact.store';
import { DeleteComponent as DeleteExternalContactComponent } from './external-contacts-tab/delete/delete.component';
import { ExternalContactsDetailsComponent } from './external-contacts-tab/details/details.component';
import { ExternalContactDetailsGuard } from './external-contacts-tab/details/details.guard';
import { RegulatorsComponent } from './regulators.component';
import { AddConfirmationComponent } from './regulators-users-tab/add-confirmation/add-confirmation.component';
import { DeleteComponent as DeleteRegulatorComponent } from './regulators-users-tab/delete/delete.component';
import { CanAddUsers } from './regulators-users-tab/details/can-add-users.guard';
import { CanEditUserGuard, ResetRegulatorDetails } from './regulators-users-tab/details/can-edit-user.guard';
import { DetailsComponent } from './regulators-users-tab/details/details.component';
import { DetailsStore } from './regulators-users-tab/details/details.store';
import { SignatureFileDownloadComponent } from './regulators-users-tab/file-download/signature-file-download.component';
import { SiteContactsComponent } from './site-contacts-tab/site-contacts.component';

export const REGULATOR_ROUTES: Routes = [
  {
    path: '',
    title: 'Regulator users',
    component: RegulatorsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'add-confirmation',
    title: 'Account confirmation email sent',
    component: AddConfirmationComponent,
  },
  {
    path: 'add',
    title: 'Add a new user',
    data: { breadcrumb: false, backlink: '../' },
    providers: [DetailsStore],
    component: DetailsComponent,
    canActivate: [CanAddUsers],
    canDeactivate: [PendingRequestGuard, ResetRegulatorDetails],
  },
  {
    path: ':userId',
    canActivate: [CanEditUserGuard],
    providers: [DetailsStore],
    resolve: { user: () => inject(DetailsStore).state.user },
    canDeactivate: [ResetRegulatorDetails],
    children: [
      {
        path: '',
        title: 'User details',
        data: {
          breadcrumb: false,
          backlink: '../',
        },
        pathMatch: 'full',
        component: DetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        title: 'Confirm that this user account will be deleted',
        data: {
          backlink: '../..',
          breadcrumb: false,
        },
        component: DeleteRegulatorComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: '2fa',
        loadChildren: () => import('../two-fa/two-fa.routes').then((m) => m.TWO_FA_ROUTES),
      },
      {
        path: 'file-download/:uuid',
        title: 'Your download has started',
        component: SignatureFileDownloadComponent,
      },
    ],
  },
  {
    path: 'file-download/:uuid',
    title: 'Your download has started',
    component: SignatureFileDownloadComponent,
  },
  {
    path: 'external-contacts',
    providers: [ActiveExternalContactStore],
    children: [
      {
        path: 'add',
        title: 'Add an external contact',
        data: { breadcrumb: false, backlink: '../..' },
        component: ExternalContactsDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: ':userId',
        canActivate: [ExternalContactDetailsGuard],
        canDeactivate: [() => inject(ActiveExternalContactStore).reset()],
        children: [
          {
            path: '',
            title: 'External contact details',
            pathMatch: 'full',
            data: {
              breadcrumb: false,
              backlink: '../..',
            },
            component: ExternalContactsDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete',
            title: 'Confirm that this external contact will be deleted',
            data: {
              breadcrumb: false,
              backlink: '../../..',
            },
            component: DeleteExternalContactComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'site-contacts',
    title: 'Site contacts',
    component: SiteContactsComponent,
  },
];

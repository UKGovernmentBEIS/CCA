import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

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
    data: { pageTitle: 'Regulator users' },
    component: RegulatorsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'add-confirmation',
    component: AddConfirmationComponent,
  },
  {
    path: 'add',
    data: { pageTitle: 'Add a new user', breadcrumb: false, backlink: '../' },
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
        data: {
          breadcrumb: false,
          backlink: '../',
          pageTitle: 'User details',
        },
        pathMatch: 'full',
        component: DetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: {
          pageTitle: 'Confirm that this user account will be deleted',
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
        component: SignatureFileDownloadComponent,
      },
    ],
  },
  {
    path: 'file-download/:uuid',
    component: SignatureFileDownloadComponent,
  },
  {
    path: 'external-contacts',
    providers: [ActiveExternalContactStore],
    children: [
      {
        path: 'add',
        data: { pageTitle: 'Add an external contact', breadcrumb: false, backlink: '../..' },
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
            pathMatch: 'full',
            data: {
              pageTitle: 'External contact details',
              breadcrumb: false,
              backlink: '../..',
            },
            component: ExternalContactsDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete',
            data: {
              pageTitle: 'Confirm that this external contact will be deleted',
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
    component: SiteContactsComponent,
  },
];

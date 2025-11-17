import { Routes } from '@angular/router';

import {
  CanActivateTargetUnitCreationStep,
  CanActivateTargetUnitCreationSummary,
  ResetCreateUnitAccountStore,
} from './create-target-unit.guard';
import { CreateTargetUnitStore } from './create-target-unit.store';
import { CreateTargetUnitSubSectorResolver } from './create-target-unit-subsector.resolver';

export const CREATE_TARGET_UNIT_ROUTES: Routes = [
  {
    path: 'create',
    title: 'Target unit creation',
    resolve: { subSectorScheme: CreateTargetUnitSubSectorResolver },
    providers: [CreateTargetUnitStore],
    canDeactivate: [ResetCreateUnitAccountStore],
    children: [
      {
        path: 'company-registration-number',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./company-registration-number/company-registration-number.component').then(
            (c) => c.CompanyRegistrationNumberComponent,
          ),
      },
      {
        path: 'target-unit-details',
        data: { backlink: '../company-registration-number', breadcrumb: false },
        loadComponent: () => import('./create-target-unit.component').then((c) => c.CreateTargetUnitComponent),
      },
      {
        path: 'operator-address',
        data: { backlink: '../target-unit-details', breadcrumb: false },
        canActivate: [CanActivateTargetUnitCreationStep],
        loadComponent: () =>
          import('./operator-address/operator-address.component').then((c) => c.OperatorAddressComponent),
      },
      {
        path: 'responsible-person',
        data: { backlink: '../operator-address', breadcrumb: false },
        canActivate: [CanActivateTargetUnitCreationStep],
        loadComponent: () =>
          import('./responsible-person/responsible-person.component').then((c) => c.ResponsiblePersonComponent),
      },
      {
        path: 'administrative-contact',
        data: { backlink: '../responsible-person', breadcrumb: false },
        canActivate: [CanActivateTargetUnitCreationStep],
        loadComponent: () =>
          import('./administrative-contact/administrative-contact.component').then(
            (c) => c.AdministrativeContactComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: false },
        canActivate: [CanActivateTargetUnitCreationSummary],
        loadComponent: () =>
          import('./summary/create-target-unit-summary.component').then((c) => c.CreateTargetUnitSummaryComponent),
      },
      {
        path: 'confirmation',
        data: { breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
      },
      {
        path: '**',
        redirectTo: 'company-registration-number',
      },
    ],
  },
];

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
    title: 'Target unit creation',
    path: 'create',
    canDeactivate: [ResetCreateUnitAccountStore],
    resolve: { subSectorScheme: CreateTargetUnitSubSectorResolver },
    children: [
      {
        title: 'Does the operator have a company number?',
        path: 'company-registration-number',
        loadComponent: () =>
          import('./company-registration-number/company-registration-number.component').then(
            (c) => c.CompanyRegistrationNumberComponent,
          ),
        data: { backlink: { link: '../../../', fragment: 'target-units' }, breadcrumb: false },
      },
      {
        title: 'Target unit details',
        path: 'target-unit-details',
        loadComponent: () => import('./create-target-unit.component').then((c) => c.CreateTargetUnitComponent),
        data: { backlink: '../company-registration-number', breadcrumb: false },
      },
      {
        title: 'Operator address',
        path: 'operator-address',
        loadComponent: () =>
          import('./operator-address/operator-address.component').then((c) => c.OperatorAddressComponent),
        canActivate: [CanActivateTargetUnitCreationStep],
        data: { backlink: '../target-unit-details', breadcrumb: false },
      },
      {
        title: 'Responsible person',
        path: 'responsible-person',
        loadComponent: () =>
          import('./responsible-person/responsible-person.component').then((c) => c.ResponsiblePersonComponent),
        canActivate: [CanActivateTargetUnitCreationStep],
        data: { backlink: '../operator-address', breadcrumb: false },
      },
      {
        title: 'Administrative contact details',
        path: 'administrative-contact',
        loadComponent: () =>
          import('./administrative-contact/administrative-contact.component').then(
            (c) => c.AdministrativeContactComponent,
          ),
        canActivate: [CanActivateTargetUnitCreationStep],
        data: { backlink: '../responsible-person', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'summary',
        loadComponent: () =>
          import('./summary/create-target-unit-summary.component').then((c) => c.CreateTargetUnitSummaryComponent),
        canActivate: [CanActivateTargetUnitCreationSummary],
        data: { breadcrumb: false },
      },
      {
        title: 'Target unit created',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
        data: { breadcrumb: false },
      },
      {
        path: '**',
        redirectTo: 'company-registration-number',
      },
    ],
    providers: [CreateTargetUnitStore],
  },
];

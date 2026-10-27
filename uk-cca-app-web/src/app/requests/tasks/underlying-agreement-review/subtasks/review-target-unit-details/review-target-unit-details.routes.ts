import { Routes } from '@angular/router';

import { ReviewTargetUnitDetailsWizardStep } from '@requests/common';

import { reviewTargetUnitDetailsRedirectGuard } from './review-target-unit-details.guard';

export const REVIEW_TARGET_UNIT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [reviewTargetUnitDetailsRedirectGuard],
        children: [],
      },
      {
        title: 'Review target unit details',
        path: 'summary',
        loadComponent: () => import('./summary/review-target-unit-details-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Target unit details',
        path: 'decision',
        loadComponent: () => import('./decision/review-target-unit-details-decision.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-your-answers/review-target-unit-details-check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Edit company registration number',
        path: ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
        loadComponent: () =>
          import('./company-registration-number/company-registration-number.component').then(
            (c) => c.CompanyRegistrationNumberComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Edit target unit details',
        path: ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        loadComponent: () =>
          import('./target-unit-details/target-unit-details.component').then((c) => c.TargetUnitDetailsComponent),
        data: { backlink: `../${ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER}`, breadcrumb: false },
      },
      {
        title: 'Edit operator address',
        path: ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS,
        loadComponent: () =>
          import('./operator-address/operator-address.component').then((c) => c.OperatorAddressComponent),
        data: { backlink: `../${ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS}`, breadcrumb: false },
      },
      {
        title: 'Edit responsible person',
        path: ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON,
        loadComponent: () =>
          import('./responsible-person/responsible-person.component').then((c) => c.ResponsiblePersonComponent),
        data: { backlink: `../${ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS}`, breadcrumb: false },
      },
    ],
  },
];

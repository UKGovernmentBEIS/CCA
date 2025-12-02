import { Routes } from '@angular/router';

import { ReviewTargetUnitDetailsWizardStep } from '@requests/common';

import { reviewTargetUnitDetailsRedirectGuard } from './review-target-unit-details-redirect.guard';

export const REVIEW_TARGET_UNIT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [reviewTargetUnitDetailsRedirectGuard],
        children: [],
      },
      {
        path: 'summary',
        title: 'Review target unit details',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./summary/review-target-unit-details-summary.component').then(
            (c) => c.ReviewTargetUnitDetailsSummaryComponent,
          ),
      },
      {
        path: 'decision',
        title: 'Target unit details decision',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./decision/review-target-unit-details-decision.component').then(
            (c) => c.ReviewTargetUnitDetailsDecisionComponent,
          ),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/review-target-unit-details-check-your-answers.component').then(
            (c) => c.ReviewTargetUnitDetailsCheckYourAnswersComponent,
          ),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
        title: 'Edit company registration number',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./company-registration-number/company-registration-number.component').then(
            (c) => c.CompanyRegistrationNumberComponent,
          ),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        title: 'Edit target unit details',
        data: { backlink: `../${ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER}`, breadcrumb: false },
        loadComponent: () =>
          import('./target-unit-details/target-unit-details.component').then((c) => c.TargetUnitDetailsComponent),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS,
        title: 'Edit operator address',
        data: { backlink: `../${ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS}`, breadcrumb: false },
        loadComponent: () =>
          import('./operator-address/operator-address.component').then((c) => c.OperatorAddressComponent),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON,
        title: 'Edit responsible person',
        data: { backlink: `../${ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS}`, breadcrumb: false },
        loadComponent: () =>
          import('./responsible-person/responsible-person.component').then((c) => c.ResponsiblePersonComponent),
      },
    ],
  },
];

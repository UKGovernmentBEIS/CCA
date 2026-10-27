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
        title: 'Review target unit details',
        path: 'summary',
        loadComponent: () => import('./summary/review-target-unit-details-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-your-answers/review-target-unit-details-check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Edit target unit details',
        path: ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        loadComponent: () => import('./target-unit-details/target-unit-details.component'),
        data: { backlink: '../', breadcrumb: false },
      },
      {
        title: 'Edit operator address',
        path: ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS,
        loadComponent: () => import('./operator-address/operator-address.component'),
        data: { backlink: '../', breadcrumb: false },
      },
      {
        title: 'Edit responsible person',
        path: ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON,
        loadComponent: () => import('./responsible-person/responsible-person.component'),
        data: { backlink: '../', breadcrumb: false },
      },
    ],
  },
];

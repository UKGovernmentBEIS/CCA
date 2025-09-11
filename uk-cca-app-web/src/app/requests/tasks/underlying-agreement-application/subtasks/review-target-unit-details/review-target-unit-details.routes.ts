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
        path: 'summary',
        title: 'Review target unit details',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/review-target-unit-details-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/review-target-unit-details-check-your-answers.component').then(
            (m) => m.ReviewTargetUnitDetailsCheckYourAnswersComponent,
          ),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        title: 'Edit target unit details',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () =>
          import('./target-unit-details/target-unit-details.component').then((m) => m.TargetUnitDetailsComponent),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS,
        title: 'Edit operator address',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () =>
          import('./operator-address/operator-address.component').then((m) => m.OperatorAddressComponent),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON,
        title: 'Edit responsible person',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () =>
          import('./responsible-person/responsible-person.component').then((m) => m.ResponsiblePersonComponent),
      },
    ],
  },
];

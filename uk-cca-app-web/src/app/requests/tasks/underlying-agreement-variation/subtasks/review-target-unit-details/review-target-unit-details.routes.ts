import { Routes } from '@angular/router';

import {
  canActivateTargetUnitDetails,
  canActivateTargetUnitDetailsCheckYourAnswers,
  canActivateTargetUnitDetailsSummary,
  ReviewTargetUnitDetailsWizardStep,
} from '@requests/common';

export const REVIEW_TARGET_UNIT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: ReviewTargetUnitDetailsWizardStep.SUMMARY,
        title: 'Review target unit details',
        canActivate: [canActivateTargetUnitDetailsSummary],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/review-target-unit-details-summary.component'),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        canActivate: [canActivateTargetUnitDetailsCheckYourAnswers],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/review-target-unit-details-check-your-answers.component'),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        title: 'Edit target unit details',
        canActivate: [canActivateTargetUnitDetails],
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((m) => m.TargetUnitDetailsSubmitComponent),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS,
        title: 'Edit operator address',
        canActivate: [canActivateTargetUnitDetails],
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((m) => m.OperatorAddressComponent),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON,
        title: 'Edit responsible person',
        canActivate: [canActivateTargetUnitDetails],
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((m) => m.ResponsiblePersonComponent),
      },
      {
        path: '**',
        redirectTo: ReviewTargetUnitDetailsWizardStep.SUMMARY,
      },
    ],
  },
];

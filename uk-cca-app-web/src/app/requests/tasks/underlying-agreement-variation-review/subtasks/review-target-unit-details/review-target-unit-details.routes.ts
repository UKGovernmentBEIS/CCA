import { Routes } from '@angular/router';

import { ReviewTargetUnitDetailsReviewWizardStep } from '@requests/common';

import {
  canActivateTargetUnitDetails,
  canActivateTargetUnitDetailsCheckYourAnswers,
  canActivateTargetUnitDetailsDecision,
  canActivateTargetUnitDetailsSummary,
} from './review-target-unit-details.guard';

export const REVIEW_TARGET_UNIT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: ReviewTargetUnitDetailsReviewWizardStep.SUMMARY,
        title: 'Review target unit details',
        canActivate: [canActivateTargetUnitDetailsSummary],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/review-target-unit-details-summary.component'),
      },
      {
        path: ReviewTargetUnitDetailsReviewWizardStep.DECISION,
        title: 'Target unit details',
        canActivate: [canActivateTargetUnitDetailsDecision],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./decision/review-target-unit-details-decision.component'),
      },
      {
        path: ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        canActivate: [canActivateTargetUnitDetailsCheckYourAnswers],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/review-target-unit-details-check-your-answers.component'),
      },
      {
        path: ReviewTargetUnitDetailsReviewWizardStep.TARGET_UNIT_DETAILS,
        title: 'Edit target unit details',
        canActivate: [canActivateTargetUnitDetails],
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((m) => m.TargetUnitDetailsReviewComponent),
      },
      {
        path: ReviewTargetUnitDetailsReviewWizardStep.OPERATOR_ADDRESS,
        title: 'Edit operator address',
        canActivate: [canActivateTargetUnitDetails],
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((m) => m.OperatorAddressComponent),
      },
      {
        path: ReviewTargetUnitDetailsReviewWizardStep.RESPONSIBLE_PERSON,
        title: 'Edit responsible person',
        canActivate: [canActivateTargetUnitDetails],
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((m) => m.ResponsiblePersonComponent),
      },
      {
        path: '**',
        redirectTo: ReviewTargetUnitDetailsReviewWizardStep.DECISION,
      },
    ],
  },
];

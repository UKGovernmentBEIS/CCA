import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  canActivateTargetPeriodDecision,
  canActivateTargetPeriodReview,
} from '@requests/common';

import { canActivateTargetPeriodCheckYourAnswers, canActivateTargetPeriodSummary } from './tp-5.guard';

export const TARGET_PERIOD_5_ROUTES: Routes = [
  {
    path: '',
    providers: [
      {
        provide: BASELINE_AND_TARGETS_SUBTASK,
        useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
      },
    ],
    children: [
      {
        path: BaseLineAndTargetsReviewStep.BASELINE_EXISTS,
        title: 'Baseline data exists',
        data: { backlink: `../../../`, breadcrumb: false },
        canActivate: [canActivateTargetPeriodReview],
        loadComponent: () => import('@requests/common').then((c) => c.BaselineExistsComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.BASELINE_EXISTS}`, breadcrumb: false },
        canActivate: [canActivateTargetPeriodReview],
        loadComponent: () => import('@requests/common').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.TARGET_COMPOSITION}`, breadcrumb: false },
        canActivate: [canActivateTargetPeriodReview],
        loadComponent: () => import('@requests/common').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_TARGETS,
        title: 'Add targets',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA}`, breadcrumb: false },
        canActivate: [canActivateTargetPeriodReview],
        loadComponent: () => import('@requests/common').then((c) => c.AddTargetsComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateTargetPeriodCheckYourAnswers],
        loadComponent: () =>
          import('./check-your-answers/tp5-check-your-answers.component').then((c) => c.TP5CheckYourAnswersComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateTargetPeriodSummary],
        loadComponent: () => import('./summary/tp5-summary.component').then((c) => c.TP5SummaryComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.DECISION,
        title: 'TP6 (2024)',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateTargetPeriodDecision],
        loadComponent: () => import('./decision/tp5-decision.component').then((c) => c.TP5DecisionComponent),
      },
      {
        path: '**',
        redirectTo: BaseLineAndTargetsReviewStep.DECISION,
      },
    ],
  },
];

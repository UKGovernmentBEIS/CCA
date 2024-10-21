import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
} from '@requests/common';

import {
  canActivateTargetPeriod,
  canActivateTargetPeriodCheckYourAnswers,
  canActivateTargetPeriodDecision,
  canActivateTargetPeriodSummary,
} from './tp-6.guard';

export const TARGET_PERIOD_6_ROUTES: Routes = [
  {
    path: '',
    providers: [
      {
        provide: BASELINE_AND_TARGETS_SUBTASK,
        useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
      },
    ],
    children: [
      {
        path: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../../../`, breadcrumb: false },
        canActivate: [canActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.TARGET_COMPOSITION}`, breadcrumb: false },
        canActivate: [canActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_TARGETS,
        title: 'Add targets',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA}`, breadcrumb: false },
        canActivate: [canActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.AddTargetsComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateTargetPeriodCheckYourAnswers],
        loadComponent: () =>
          import('./check-your-answers/tp6-check-your-answers.component').then((c) => c.TP6CheckYourAnswersComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateTargetPeriodSummary],
        loadComponent: () => import('./summary/tp6-summary.component').then((c) => c.TP6SummaryComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.DECISION,
        title: 'TP6 (2024)',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateTargetPeriodDecision],
        loadComponent: () => import('./decision/tp6-decision.component').then((c) => c.TP6DecisionComponent),
      },
      {
        path: '**',
        redirectTo: BaseLineAndTargetsReviewStep.DECISION,
      },
    ],
  },
];

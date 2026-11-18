import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
} from '@requests/common';

import { tp5RedirectGuard } from './tp5-redirect.guard';

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
        path: '',
        canActivate: [tp5RedirectGuard],
        children: [],
      },
      {
        path: BaseLineAndTargetsReviewStep.BASELINE_EXISTS,
        title: 'Baseline data exists',
        data: { backlink: `../../../`, breadcrumb: false },
        loadComponent: () =>
          import('./baseline-exists/baseline-exists.component').then((c) => c.BaselineExistsComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.BASELINE_EXISTS}`, breadcrumb: false },
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.TARGET_COMPOSITION}`, breadcrumb: false },
        loadComponent: () =>
          import('./add-baseline-data/add-baseline-data.component').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_TARGETS,
        title: 'Add targets',
        data: { backlink: `../${BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA}`, breadcrumb: false },
        loadComponent: () => import('./add-targets/add-targets.component').then((c) => c.AddTargetsComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/tp5-check-your-answers.component').then((c) => c.TP5CheckYourAnswersComponent),
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/tp5-summary.component').then((c) => c.TP5SummaryComponent),
      },
      {
        path: 'decision',
        title: 'TP5 (2021-2023)',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./decision/tp5-decision.component').then((c) => c.TP5DecisionComponent),
      },
    ],
  },
];

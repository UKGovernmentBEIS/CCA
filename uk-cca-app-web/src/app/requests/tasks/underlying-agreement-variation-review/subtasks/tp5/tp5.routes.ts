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
    children: [
      {
        path: '',
        canActivate: [tp5RedirectGuard],
        children: [],
      },
      {
        title: 'Baseline data exists',
        path: BaseLineAndTargetsReviewStep.BASELINE_EXISTS,
        loadComponent: () =>
          import('./baseline-exists/baseline-exists.component').then((c) => c.BaselineExistsComponent),
        data: { backlink: `../../../`, breadcrumb: false },
      },
      {
        title: 'Add target composition',
        path: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
        data: { backlink: `../${BaseLineAndTargetsReviewStep.BASELINE_EXISTS}`, breadcrumb: false },
      },
      {
        title: 'Add baseline data',
        path: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
        loadComponent: () =>
          import('./add-baseline-data/add-baseline-data.component').then((c) => c.AddBaselineDataComponent),
        data: { backlink: `../${BaseLineAndTargetsReviewStep.TARGET_COMPOSITION}`, breadcrumb: false },
      },
      {
        title: 'Add targets',
        path: BaseLineAndTargetsReviewStep.ADD_TARGETS,
        loadComponent: () => import('./add-targets/add-targets.component').then((c) => c.AddTargetsComponent),
        data: { backlink: `../${BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA}`, breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/tp5-check-your-answers.component').then((c) => c.TP5CheckYourAnswersComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/tp5-summary.component').then((c) => c.TP5SummaryComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'TP5 (2021-2023)',
        path: 'decision',
        loadComponent: () => import('./decision/tp5-decision.component').then((c) => c.TP5DecisionComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
    providers: [
      {
        provide: BASELINE_AND_TARGETS_SUBTASK,
        useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
      },
    ],
  },
];

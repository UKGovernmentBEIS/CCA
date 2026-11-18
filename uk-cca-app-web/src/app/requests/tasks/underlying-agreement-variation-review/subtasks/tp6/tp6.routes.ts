import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
} from '@requests/common';

import { tp6RedirectGuard } from './tp6-redirect.guard';

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
        path: '',
        canActivate: [tp6RedirectGuard],
        children: [],
      },
      {
        path: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
        title: 'Target composition',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () =>
          import('./add-baseline-data/add-baseline-data.component').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsReviewStep.ADD_TARGETS,
        title: 'Add targets',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('./add-targets/add-targets.component').then((c) => c.AddTargetsComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/tp6-check-your-answers.component').then((c) => c.TP6CheckYourAnswersComponent),
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/tp6-summary.component').then((c) => c.TP6SummaryComponent),
      },
      {
        path: 'decision',
        title: 'TP6 (2024)',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./decision/tp6-decision.component').then((c) => c.TP6DecisionComponent),
      },
    ],
  },
];

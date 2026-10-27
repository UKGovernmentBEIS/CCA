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
    children: [
      {
        path: '',
        canActivate: [tp6RedirectGuard],
        children: [],
      },
      {
        title: 'Target composition',
        path: BaseLineAndTargetsReviewStep.TARGET_COMPOSITION,
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Add baseline data',
        path: BaseLineAndTargetsReviewStep.ADD_BASELINE_DATA,
        loadComponent: () =>
          import('./add-baseline-data/add-baseline-data.component').then((c) => c.AddBaselineDataComponent),
        data: { backlink: '../', breadcrumb: false },
      },
      {
        title: 'Add targets',
        path: BaseLineAndTargetsReviewStep.ADD_TARGETS,
        loadComponent: () => import('./add-targets/add-targets.component').then((c) => c.AddTargetsComponent),
        data: { backlink: '../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/tp6-check-your-answers.component').then((c) => c.TP6CheckYourAnswersComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/tp6-summary.component').then((c) => c.TP6SummaryComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'TP6 (2024)',
        path: 'decision',
        loadComponent: () => import('./decision/tp6-decision.component').then((c) => c.TP6DecisionComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
    providers: [
      {
        provide: BASELINE_AND_TARGETS_SUBTASK,
        useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
      },
    ],
  },
];

import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
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
        path: BaseLineAndTargetsStep.BASELINE_EXISTS,
        title: 'Baseline data exists',
        data: { backlink: `../../../`, breadcrumb: false },
        loadComponent: () =>
          import('./baseline-exists/baseline-exists.component').then((c) => c.BaselineExistsComponent),
      },
      {
        path: BaseLineAndTargetsStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../${BaseLineAndTargetsStep.BASELINE_EXISTS}`, breadcrumb: false },
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        data: { backlink: `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`, breadcrumb: false },
        loadComponent: () =>
          import('./add-baseline-data/add-baseline-data.component').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_TARGETS,
        title: 'Add targets',
        data: { backlink: `../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`, breadcrumb: false },
        loadComponent: () => import('./add-targets/add-targets.component').then((c) => c.AddTargetsComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/tp5-check-your-answers.component').then((c) => c.Tp5CheckYourAnswersComponent),
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./summary/baseline-and-targets-summary.component').then((c) => c.BaselineAndTargetsSummaryComponent),
      },
    ],
  },
];

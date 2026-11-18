import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
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
        path: BaseLineAndTargetsStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../../../`, breadcrumb: false },
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
          import('./check-your-answers/tp6-check-your-answers.component').then((c) => c.Tp6CheckYourAnswersComponent),
      },
      {
        path: 'summary',
        data: { backlink: '../../../', breadcrumb: false },
        title: 'Summary',
        loadComponent: () =>
          import('./summary/baseline-and-targets-summary.component').then((c) => c.BaselineAndTargetsSummaryComponent),
      },
    ],
  },
];

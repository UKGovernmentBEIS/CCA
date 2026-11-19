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
    children: [
      {
        path: '',
        canActivate: [tp6RedirectGuard],
        children: [],
      },
      {
        title: 'Add target composition',
        path: BaseLineAndTargetsStep.TARGET_COMPOSITION,
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
        data: { backlink: `../../../`, breadcrumb: false },
      },
      {
        title: 'Add baseline data',
        path: BaseLineAndTargetsStep.ADD_BASELINE_DATA,
        loadComponent: () =>
          import('./add-baseline-data/add-baseline-data.component').then((c) => c.AddBaselineDataComponent),
        data: { backlink: `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`, breadcrumb: false },
      },
      {
        title: 'Add targets',
        path: BaseLineAndTargetsStep.ADD_TARGETS,
        loadComponent: () => import('./add-targets/add-targets.component').then((c) => c.AddTargetsComponent),
        data: { backlink: `../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`, breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/tp6-check-your-answers.component').then((c) => c.Tp6CheckYourAnswersComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/baseline-and-targets-summary.component').then((c) => c.BaselineAndTargetsSummaryComponent),
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

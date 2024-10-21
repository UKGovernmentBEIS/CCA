import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  CanActivateTargetPeriod,
  CanActivateTargetPeriodCheckYourAnswers,
  CanActivateTargetPeriodSummary,
} from '@requests/common';

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
        path: BaseLineAndTargetsStep.BASELINE_EXISTS,
        title: 'Baseline data exists',
        data: { backlink: `../../../`, breadcrumb: false },
        canActivate: [CanActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.BaselineExistsComponent),
      },
      {
        path: BaseLineAndTargetsStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../${BaseLineAndTargetsStep.BASELINE_EXISTS}`, breadcrumb: false },
        canActivate: [CanActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        canActivate: [CanActivateTargetPeriod],
        data: { backlink: `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`, breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_TARGETS,
        title: 'Add targets',
        canActivate: [CanActivateTargetPeriod],
        data: { backlink: `../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`, breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.AddTargetsComponent),
      },
      {
        path: BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        canActivate: [CanActivateTargetPeriodCheckYourAnswers],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('../common/check-your-answers/baseline-and-targets-check-your-answers.component').then(
            (c) => c.BaselineAndTargetsCheckYourAnswersComponent,
          ),
      },
      {
        path: BaseLineAndTargetsStep.SUMMARY,
        title: 'Summary',
        canActivate: [CanActivateTargetPeriodSummary],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('../common/summary/baseline-and-targets-summary.component').then(
            (c) => c.BaselineAndTargetsSummaryComponent,
          ),
      },
      {
        path: '**',
        redirectTo: BaseLineAndTargetsStep.BASELINE_EXISTS,
      },
    ],
  },
];

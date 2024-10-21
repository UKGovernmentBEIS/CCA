import { Routes } from '@angular/router';

import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  CanActivateTargetPeriod,
  CanActivateTargetPeriodCheckYourAnswers,
  CanActivateTargetPeriodSummary,
} from '@requests/common';

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
        path: BaseLineAndTargetsStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../../../`, breadcrumb: false },
        canActivate: [CanActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        data: { backlink: `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`, breadcrumb: false },
        canActivate: [CanActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_TARGETS,
        title: 'Add targets',
        data: { backlink: `../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`, breadcrumb: false },
        canActivate: [CanActivateTargetPeriod],
        loadComponent: () => import('@requests/common').then((c) => c.AddTargetsComponent),
      },
      {
        path: BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateTargetPeriodCheckYourAnswers],
        loadComponent: () =>
          import('../common/check-your-answers/baseline-and-targets-check-your-answers.component').then(
            (c) => c.BaselineAndTargetsCheckYourAnswersComponent,
          ),
      },
      {
        path: BaseLineAndTargetsStep.SUMMARY,
        data: { backlink: '../../../', breadcrumb: false },
        title: 'Summary',
        canActivate: [CanActivateTargetPeriodSummary],
        loadComponent: () =>
          import('../common/summary/baseline-and-targets-summary.component').then(
            (c) => c.BaselineAndTargetsSummaryComponent,
          ),
      },
      {
        path: '**',
        redirectTo: BaseLineAndTargetsStep.TARGET_COMPOSITION,
      },
    ],
  },
];

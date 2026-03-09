import { Routes } from '@angular/router';

import { OverallDecisionWizardStep } from '@requests/common';

import { canActivateOperatorAssentDecision } from './operator-assent-decision.guard';

export const OPERATOR_ASSENT_DECISION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [canActivateOperatorAssentDecision],
        children: [],
      },
      {
        path: OverallDecisionWizardStep.ADDITIONAL_INFO,
        data: { backlink: `../../../`, breadcrumb: false },
        loadComponent: () =>
          import('./additional-info/additional-info.component').then((c) => c.AdditionalInfoComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/overall-decision-check-your-answers.component').then(
            (c) => c.OverallDecisionCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./summary/overall-decision-summary.component').then((c) => c.OverallDecisionSummaryComponent),
      },
    ],
  },
];

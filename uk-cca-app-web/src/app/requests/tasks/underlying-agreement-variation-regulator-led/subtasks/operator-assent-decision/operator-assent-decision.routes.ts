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
        title: 'Provide any additional information here to support your decision',
        path: OverallDecisionWizardStep.ADDITIONAL_INFO,
        loadComponent: () =>
          import('./additional-info/additional-info.component').then((c) => c.AdditionalInfoComponent),
        data: { backlink: `../../../`, breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/overall-decision-check-your-answers.component').then(
            (c) => c.OverallDecisionCheckYourAnswersComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/overall-decision-summary.component').then((c) => c.OverallDecisionSummaryComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];

import { Routes } from '@angular/router';

import { OverallDecisionWizardStep } from '@requests/common';

import { canActivateVariationOverallDecision } from './overall-decision.guard';

export const OVERALL_DECISION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [canActivateVariationOverallDecision],
        children: [],
      },
      {
        path: OverallDecisionWizardStep.AVAILABLE_ACTIONS,
        data: { backlink: `../../../`, breadcrumb: false },
        title: 'Overall decision',
        loadComponent: () =>
          import('./available-actions/available-actions.component').then((c) => c.AvailableActionsComponent),
      },
      {
        path: OverallDecisionWizardStep.EXPLANATION,
        data: { backlink: `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`, breadcrumb: false },
        title: 'Explain why you are rejecting the application',
        loadComponent: () =>
          import('./explanation/explanation-component.component').then((c) => c.ExplanationComponentComponent),
      },
      {
        path: OverallDecisionWizardStep.ADDITIONAL_INFO,
        data: { backlink: `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`, breadcrumb: false },
        title: 'Provide any additional information here to support your decision (optional)',
        loadComponent: () =>
          import('./additional-info/additional-info.component').then((c) => c.AdditionalInfoComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: `../${OverallDecisionWizardStep.ADDITIONAL_INFO}`, breadcrumb: false },
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

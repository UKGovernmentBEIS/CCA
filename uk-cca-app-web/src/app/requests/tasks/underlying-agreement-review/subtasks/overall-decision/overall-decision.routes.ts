import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { OverallDecisionWizardStep } from '@requests/common';

import {
  canActivateOverallDecision,
  canActivateOverallDecisionSummary,
  initializeOverallDecisionStore,
} from './overall-decision.guard';
import { OverallDecisionStore } from './overall-decision.store';

export const OVERALL_DECISION_ROUTES: Routes = [
  {
    path: '',
    providers: [OverallDecisionStore],
    canActivate: [initializeOverallDecisionStore],
    canDeactivate: [() => inject(OverallDecisionStore).reset()],
    children: [
      {
        path: OverallDecisionWizardStep.AVAILABLE_ACTIONS,
        title: 'Overall decision',
        loadComponent: () =>
          import('./available-actions/available-actions.component').then((c) => c.AvailableActionsComponent),
      },
      {
        path: OverallDecisionWizardStep.EXPLANATION,
        title: 'Explain why you are rejecting the application',
        canActivate: [canActivateOverallDecision],
        loadComponent: () =>
          import('./explanation/explanation-component.component').then((c) => c.ExplanationComponentComponent),
      },
      {
        path: OverallDecisionWizardStep.ADDITIONAL_INFO,
        title: 'Provide any additional information here to support your decision (optional)',
        canActivate: [canActivateOverallDecision],
        loadComponent: () =>
          import('./additional-info/additional-info.component').then((c) => c.AdditionalInfoComponent),
      },
      {
        path: OverallDecisionWizardStep.CHECK_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateOverallDecision],
        loadComponent: () =>
          import('./check-your-answers/overall-decision-check-your-answers.component').then(
            (c) => c.OverallDecisionCheckYourAnswersComponent,
          ),
      },
      {
        path: OverallDecisionWizardStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateOverallDecisionSummary],
        loadComponent: () =>
          import('./summary/overall-decision-summary.component').then((c) => c.OverallDecisionSummaryComponent),
      },

      {
        path: '**',
        redirectTo: OverallDecisionWizardStep.SUMMARY,
      },
    ],
  },
];

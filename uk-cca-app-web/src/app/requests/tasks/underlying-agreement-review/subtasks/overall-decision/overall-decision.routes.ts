import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { OverallDecisionWizardStep, underlyingAgreementReviewQuery } from '@requests/common';

import { canActivateOverallDecision } from './overall-decision.guard';

export const OVERALL_DECISION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [canActivateOverallDecision],
        children: [],
      },
      {
        title: 'Overall decision',
        path: OverallDecisionWizardStep.AVAILABLE_ACTIONS,
        loadComponent: () =>
          import('./available-actions/available-actions.component').then((c) => c.AvailableActionsComponent),
        data: { backlink: `../../../`, breadcrumb: false },
      },
      {
        title: 'Explain why you are rejecting the application',
        path: OverallDecisionWizardStep.EXPLANATION,
        loadComponent: () =>
          import('./explanation/explanation-component.component').then((c) => c.ExplanationComponentComponent),
        data: { backlink: `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`, breadcrumb: false },
      },
      {
        title: 'Provide any additional information here to support your decision (optional)',
        path: OverallDecisionWizardStep.ADDITIONAL_INFO,
        loadComponent: () =>
          import('./additional-info/additional-info.component').then((c) => c.AdditionalInfoComponent),
        data: { breadcrumb: false },
        resolve: { backlink: additionalInfoBacklinkResolver },
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

function additionalInfoBacklinkResolver(): string {
  const store = inject(RequestTaskStore);
  return store.select(underlyingAgreementReviewQuery.selectDetermination)()?.type === 'ACCEPTED'
    ? `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`
    : `../${OverallDecisionWizardStep.EXPLANATION}`;
}

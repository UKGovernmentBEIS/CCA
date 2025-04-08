import { Routes } from '@angular/router';

import {
  additionalInfoBacklinkResolver,
  canActivateOverallDecisionCheckYourAnswers,
  canActivateOverallDecisionSubtask,
  canActivateOverallDecisionSummary,
  canActivatePostDecisionSubtasks,
  OverallDecisionWizardStep,
} from '@requests/common';

export const OVERALL_DECISION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: OverallDecisionWizardStep.AVAILABLE_ACTIONS,
        data: { backlink: `../../../`, breadcrumb: false },
        title: 'Overall decision',
        canActivate: [canActivateOverallDecisionSubtask],
        loadComponent: () =>
          import('./available-actions/available-actions.component').then((c) => c.AvailableActionsComponent),
      },
      {
        path: OverallDecisionWizardStep.EXPLANATION,
        data: { backlink: `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`, breadcrumb: false },

        title: 'Explain why you are rejecting the application',
        canActivate: [canActivatePostDecisionSubtasks, canActivateOverallDecisionSubtask],
        loadComponent: () =>
          import('./explanation/explanation-component.component').then((c) => c.ExplanationComponentComponent),
      },
      {
        path: OverallDecisionWizardStep.ADDITIONAL_INFO,
        resolve: { backlink: additionalInfoBacklinkResolver },
        data: { breadcrumb: false },
        title: 'Provide any additional information here to support your decision (optional)',
        canActivate: [canActivatePostDecisionSubtasks, canActivateOverallDecisionSubtask],
        loadComponent: () =>
          import('./additional-info/additional-info.component').then((c) => c.AdditionalInfoComponent),
      },
      {
        path: OverallDecisionWizardStep.CHECK_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateOverallDecisionCheckYourAnswers],
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
          import(
            '../../../../common/underlying-agreement/modules/overall-decision/summary/overall-decision-summary.component'
          ).then((c) => c.OverallDecisionSummaryComponent),
      },
      {
        path: '**',
        redirectTo: OverallDecisionWizardStep.AVAILABLE_ACTIONS,
      },
    ],
  },
];

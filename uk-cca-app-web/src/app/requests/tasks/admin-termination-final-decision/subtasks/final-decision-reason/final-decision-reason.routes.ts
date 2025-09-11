import { Routes } from '@angular/router';

import { AdminTerminationFinalDecisionTerminateAgreementWizardStep } from '../../admin-termination-final-decision.helper';
import {
  CanActivateFinalDecisionReasonCheckYourAnswers,
  CanActivateFinalDecisionReasonStep,
  CanActivateFinalDecisionReasonSummary,
} from './final-decision-reason.guard';

export const FINAL_DECISION_REASON_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../..', breadcrumb: false },
        canActivate: [CanActivateFinalDecisionReasonSummary],
        loadComponent: () => import('./summary/final-decision-reason-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../..', breadcrumb: false },
        canActivate: [CanActivateFinalDecisionReasonCheckYourAnswers],
        loadComponent: () => import('./check-your-answers/final-decision-reason-check-your-answers.component'),
      },
      {
        path: AdminTerminationFinalDecisionTerminateAgreementWizardStep.ACTIONS,
        title: 'Admin termination final decision actions',
        data: { backlink: '../../..', breadcrumb: false },
        canActivate: [CanActivateFinalDecisionReasonStep],
        loadComponent: () => import('./actions/final-decision-reason-actions.component'),
      },
      {
        path: AdminTerminationFinalDecisionTerminateAgreementWizardStep.REASON_DETAILS,
        title: 'Admin termination final decision reason details',
        data: {
          backlink: `../${AdminTerminationFinalDecisionTerminateAgreementWizardStep.ACTIONS}`,
          breadcrumb: false,
        },
        canActivate: [CanActivateFinalDecisionReasonStep],
        loadComponent: () => import('./reason-details/final-decision-reason-details.component'),
      },
      {
        path: '**',
        redirectTo: AdminTerminationFinalDecisionTerminateAgreementWizardStep.ACTIONS,
      },
    ],
  },
];

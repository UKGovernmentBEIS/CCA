import { Routes } from '@angular/router';

import { AuthorisationAdditionalEvidenceReviewWizardStep } from '@requests/common';

import {
  canActivateAuthorisationAdditionalEvidenceCheckYourAnswers,
  canActivateAuthorisationAdditionalEvidenceDecision,
  canActivateAuthorisationAdditionalEvidenceSummary,
  canActivateAuthorisationAndAdditionalEvidence,
} from './authorisation-additional-evidence.guard';

export const AUTHORISATION_ADDITIONAL_EVIDENCE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: AuthorisationAdditionalEvidenceReviewWizardStep.DECISION,
        title: 'Authorisation and additional evidence',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateAuthorisationAdditionalEvidenceDecision],
        loadComponent: () =>
          import('./decision/authorization-additional-evidence-decision.component').then(
            (c) => c.AuthorizationAdditionalEvidenceDecisionComponent,
          ),
      },
      {
        path: AuthorisationAdditionalEvidenceReviewWizardStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateAuthorisationAdditionalEvidenceSummary],
        loadComponent: () => import('./summary/authorisation-additional-evidence-summary.component'),
      },
      {
        path: AuthorisationAdditionalEvidenceReviewWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateAuthorisationAdditionalEvidenceCheckYourAnswers],
        loadComponent: () =>
          import('./check-your-answers/authorisation-additional-evidence-check-your-answers.component'),
      },
      {
        path: AuthorisationAdditionalEvidenceReviewWizardStep.PROVIDE_EVIDENCE,
        title: 'Provide additional evidence',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateAuthorisationAndAdditionalEvidence],
        loadComponent: () => import('@requests/common').then((m) => m.ProvideEvidenceComponent),
      },
      {
        path: '**',
        redirectTo: AuthorisationAdditionalEvidenceReviewWizardStep.DECISION,
      },
    ],
  },
];

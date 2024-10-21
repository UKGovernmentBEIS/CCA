import { Routes } from '@angular/router';

import {
  AuthorisationAdditionalEvidenceWizardStep,
  CanActivateAuthorisationAdditionalEvidenceCheckYourAnswers,
  CanActivateAuthorisationAdditionalEvidenceSummary,
  CanActivateAuthorisationAndAdditionalEvidence,
} from '@requests/common';

export const AUTHORISATION_ADDITIONAL_EVIDENCE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: AuthorisationAdditionalEvidenceWizardStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateAuthorisationAdditionalEvidenceSummary],
        loadComponent: () => import('./summary/authorisation-additional-evidence-summary.component'),
      },
      {
        path: AuthorisationAdditionalEvidenceWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateAuthorisationAdditionalEvidenceCheckYourAnswers],
        loadComponent: () =>
          import('./check-your-answers/authorisation-additional-evidence-check-your-answers.component'),
      },
      {
        path: AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE,
        title: 'Provide additional evidence',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateAuthorisationAndAdditionalEvidence],
        loadComponent: () => import('@requests/common').then((m) => m.ProvideEvidenceComponent),
      },
      {
        path: '**',
        redirectTo: AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE,
      },
    ],
  },
];

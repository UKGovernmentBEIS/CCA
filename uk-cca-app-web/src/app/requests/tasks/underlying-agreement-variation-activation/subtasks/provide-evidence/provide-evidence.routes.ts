import { Routes } from '@angular/router';

import { ProvideEvidenceWizardStep } from '@requests/common';

import {
  CanActivateProvideEvidence,
  CanActivateProvideEvidenceCheckYourAnswers,
  CanActivateProvideEvidenceSummary,
} from './provide-evidence.guard';

export const PROVIDE_EVIDENCE_ROUTES: Routes = [
  {
    path: ProvideEvidenceWizardStep.DETAILS,
    title: 'Provide evidence',
    data: { backlink: '../../../', breadcrumb: false },
    canActivate: [CanActivateProvideEvidence],
    loadComponent: () => import('./details/provide-evidence-details.component'),
  },
  {
    path: ProvideEvidenceWizardStep.CHECK_ANSWERS,
    title: 'Check your answers',
    data: { backlink: '../../../', breadcrumb: false },
    canActivate: [CanActivateProvideEvidenceCheckYourAnswers],
    loadComponent: () => import('./check-answers/provide-evidence-check-answers.component'),
  },
  {
    path: ProvideEvidenceWizardStep.SUMMARY,
    data: { backlink: '../../../', breadcrumb: false },
    title: 'Summary details',
    canActivate: [CanActivateProvideEvidenceSummary],
    loadComponent: () => import('./summary/provide-evidence-summary.component'),
  },
  {
    path: '**',
    redirectTo: ProvideEvidenceWizardStep.SUMMARY,
  },
];

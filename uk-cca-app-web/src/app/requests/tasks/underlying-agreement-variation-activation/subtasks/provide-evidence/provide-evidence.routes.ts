import { Routes } from '@angular/router';

import {
  CanActivateProvideEvidence,
  CanActivateProvideEvidenceCheckYourAnswers,
  CanActivateProvideEvidenceSummary,
} from './provide-evidence.guard';

export const PROVIDE_EVIDENCE_ROUTES: Routes = [
  {
    path: 'details',
    title: 'Provide evidence',
    data: { backlink: '../../../', breadcrumb: false },
    canActivate: [CanActivateProvideEvidence],
    loadComponent: () => import('./details/provide-evidence-details.component'),
  },
  {
    path: 'check-your-answers',
    title: 'Check your answers',
    data: { backlink: '../../../', breadcrumb: false },
    canActivate: [CanActivateProvideEvidenceCheckYourAnswers],
    loadComponent: () => import('./check-answers/provide-evidence-check-answers.component'),
  },
  {
    path: 'summary',
    data: { backlink: '../../../', breadcrumb: false },
    title: 'Summary details',
    canActivate: [CanActivateProvideEvidenceSummary],
    loadComponent: () => import('./summary/provide-evidence-summary.component'),
  },
  {
    path: '**',
    redirectTo: 'summary',
  },
];

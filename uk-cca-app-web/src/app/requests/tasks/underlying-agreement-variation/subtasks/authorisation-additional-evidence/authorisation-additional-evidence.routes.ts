import { Routes } from '@angular/router';

import { AuthorisationAdditionalEvidenceWizardStep } from '@requests/common';

import { authorisationAdditionalEvidenceRedirectGuard } from './authorisation-additional-evidence-redirect.guard';

export const AUTHORISATION_ADDITIONAL_EVIDENCE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [authorisationAdditionalEvidenceRedirectGuard],
        children: [],
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/authorisation-additional-evidence-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/authorisation-additional-evidence-check-your-answers.component'),
      },
      {
        path: AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE,
        title: 'Provide additional evidence',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./provide-evidence/provide-evidence.component').then((m) => m.ProvideEvidenceComponent),
      },
    ],
  },
];

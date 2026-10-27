import { Routes } from '@angular/router';

import { AuthorisationAdditionalEvidenceWizardStep } from '@requests/common';

import { authorisationAdditionalEvidenceRedirectGuard } from './authorisation-additional-evidence.guard';

export const AUTHORISATION_ADDITIONAL_EVIDENCE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [authorisationAdditionalEvidenceRedirectGuard],
        children: [],
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/authorisation-additional-evidence-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/authorisation-additional-evidence-check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Provide additional evidence',
        path: AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE,
        loadComponent: () =>
          import('./provide-evidence/provide-evidence.component').then((c) => c.ProvideEvidenceComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];

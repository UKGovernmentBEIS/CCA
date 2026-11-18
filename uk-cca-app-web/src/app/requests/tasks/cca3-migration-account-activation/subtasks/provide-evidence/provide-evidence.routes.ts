import { Routes } from '@angular/router';

import { provideEvidenceRedirectGuard } from './provide-evidence.guard';

export const PROVIDE_EVIDENCE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [provideEvidenceRedirectGuard],
        children: [],
      },
      {
        path: 'details',
        title: 'Provide evidence',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./details/provide-evidence-details.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-answers/provide-evidence-check-answers.component'),
      },
      {
        path: 'summary',
        data: { backlink: '../../../', breadcrumb: false },
        title: 'Summary details',
        loadComponent: () => import('./summary/provide-evidence-summary.component'),
      },
      {
        path: '**',
        redirectTo: 'details',
      },
    ],
  },
];

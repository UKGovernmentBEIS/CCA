import { Routes } from '@angular/router';

import { appealOutcomeRedirectGuard } from './appeal-outcome.guard';

export const APPEAL_OUTCOME_ROUTES: Routes = [
  {
    title: 'Provide appeal outcome',
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [appealOutcomeRedirectGuard],
        children: [],
      },
      {
        title: 'Provide the appeal outcome details',
        path: 'provide-details',
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideDetailsComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
        data: { backlink: '../provide-details', breadcrumb: false },
      },
      {
        title: 'Appeal outcome completed',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
        data: { breadcrumb: false, backlink: false },
      },
    ],
  },
];

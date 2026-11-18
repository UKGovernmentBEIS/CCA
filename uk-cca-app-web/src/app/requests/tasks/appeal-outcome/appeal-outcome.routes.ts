import { Routes } from '@angular/router';

import { appealOutcomeRedirectGuard } from './appeal-outcome.guard';

export const APPEAL_OUTCOME_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [appealOutcomeRedirectGuard],
        children: [],
      },
      {
        path: 'provide-details',
        title: 'Provide the appeal outcome details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideDetailsComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../provide-details', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
      },
      {
        path: 'confirmation',
        data: { breadcrumb: false, backlink: false },
        loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
      },
    ],
  },
];

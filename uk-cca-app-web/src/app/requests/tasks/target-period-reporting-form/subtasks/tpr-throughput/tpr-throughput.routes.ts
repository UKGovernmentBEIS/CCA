import { Routes } from '@angular/router';

import { tprThroughputRedirectGuard } from './tpr-throughput-redirect.guard';

export const TPR_THROUGHPUT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [tprThroughputRedirectGuard],
        children: [],
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./summary/tpr-throughput-details-summary.component').then(
            (c) => c.TprThroughputDetailsSummaryComponent,
          ),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/tpr-throughput-details-check-your-answers.component').then(
            (c) => c.TprThroughputDetailsCheckYourAnswersComponent,
          ),
      },
      {
        path: 'details',
        title: 'Provide target period throughput details',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./details/tpr-throughput-details.component').then((c) => c.TprThroughputDetailsComponent),
      },
    ],
  },
];

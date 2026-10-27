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
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/tpr-throughput-details-summary.component').then(
            (c) => c.TprThroughputDetailsSummaryComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/tpr-throughput-details-check-your-answers.component').then(
            (c) => c.TprThroughputDetailsCheckYourAnswersComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Provide target period throughput details',
        path: 'details',
        loadComponent: () =>
          import('./details/tpr-throughput-details.component').then((c) => c.TprThroughputDetailsComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'View Products',
        path: 'products',
        loadComponent: () => import('@requests/common').then((c) => c.TprProductsComponent),
        data: { breadcrumb: false, backlink: '../details' },
      },
    ],
  },
];

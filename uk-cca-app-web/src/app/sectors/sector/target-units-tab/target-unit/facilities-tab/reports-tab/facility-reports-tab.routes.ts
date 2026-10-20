import { toggleLockGuard } from './target-period/toggle-lock/toggle-lock.guard';
import { TPRDetailsResolver } from './target-period/tpr-details/tpr-details.resolver';
import { variationSubmissionGuard } from './target-period/variation-submission/variation-submission.guard';

export const FACILITY_REPORTS_TAB_ROUTES = [
  {
    path: ':targetPeriodYear',
    resolve: { tprDetails: TPRDetailsResolver },
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./target-period/tpr-details/tpr-details.component').then((c) => c.TprDetailsComponent),
      },
      {
        path: 'products',
        data: { breadcrumb: false, backlink: '..' },
        loadComponent: () =>
          import('./target-period/tpr-details/products/tpr-products.component').then((c) => c.TprProductsComponent),
      },
      {
        path: 'toggle-lock',
        canActivate: [toggleLockGuard],
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./target-period/toggle-lock/toggle-lock.component').then((c) => c.ToggleLockComponent),
      },
      {
        path: 'variation-submission',
        canActivate: [variationSubmissionGuard],
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./target-period/variation-submission/variation-submission.component').then(
            (c) => c.VariationSubmissionComponent,
          ),
      },
    ],
  },
];

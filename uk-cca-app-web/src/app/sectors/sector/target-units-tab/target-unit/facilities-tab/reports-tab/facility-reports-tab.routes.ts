import { toggleLockGuard } from './target-period/toggle-lock/toggle-lock.guard';
import { TPRDetailsResolver } from './target-period/tpr-details/tpr-details.resolver';
import { variationSubmissionGuard } from './target-period/variation-submission/variation-submission.guard';

export const FACILITY_REPORTS_TAB_ROUTES = [
  {
    path: ':targetPeriodYear',
    children: [
      {
        path: '',
        title: 'TP report results',
        resolve: { tprDetails: TPRDetailsResolver },
        loadComponent: () =>
          import('./target-period/tpr-details/tpr-details.component').then((c) => c.TprDetailsComponent),
      },
      {
        path: 'pat-details',
        title: 'PAT report details',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () => import('./pat/pat-details/pat-details.component').then((c) => c.PatDetailsComponent),
      },
      {
        path: 'pat-details/:entryId',
        title: 'Entry details',
        data: { breadcrumb: false, backlink: '..' },
        loadComponent: () =>
          import('./pat/pat-details/entry-details/entry-details.component').then((c) => c.EntryDetailsComponent),
      },
      {
        path: 'products',
        title: 'Products',
        resolve: { tprDetails: TPRDetailsResolver },
        data: { breadcrumb: false, backlink: '..' },
        loadComponent: () =>
          import('./target-period/tpr-details/products/tpr-products.component').then((c) => c.TprProductsComponent),
      },
      {
        path: 'toggle-lock',
        title: 'Facility target period locking',
        canActivate: [toggleLockGuard],
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./target-period/toggle-lock/toggle-lock.component').then((c) => c.ToggleLockComponent),
      },
      {
        path: 'variation-submission',
        title: 'Variation submission',
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

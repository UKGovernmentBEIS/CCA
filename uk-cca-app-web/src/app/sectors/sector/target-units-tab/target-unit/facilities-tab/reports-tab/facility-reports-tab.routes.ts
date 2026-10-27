import { BreadcrumbData } from '@netz/common/navigation';

import { FacilityInfoDTO } from 'cca-api';

import { FacilityDetailsResolver } from '../facility-details.resolver';
import { toggleLockGuard } from './target-period/toggle-lock/toggle-lock.guard';
import { TPRDetailsResolver } from './target-period/tpr-details/tpr-details.resolver';
import { variationSubmissionGuard } from './target-period/variation-submission/variation-submission.guard';

const facilityReportBreadcrumb: BreadcrumbData<{ facilityDetails: FacilityInfoDTO }> = ({ facilityDetails }) => ({
  text: `${facilityDetails.facilityBusinessId}`,
  from: ':facilityId',
  fragment: 'reports',
});

export const FACILITY_REPORTS_TAB_ROUTES = [
  {
    path: ':targetPeriodYear',
    data: { breadcrumb: facilityReportBreadcrumb },
    resolve: { facilityDetails: FacilityDetailsResolver },
    children: [
      {
        title: 'TP report results',
        path: '',
        loadComponent: () =>
          import('./target-period/tpr-details/tpr-details.component').then((c) => c.TprDetailsComponent),
        resolve: { tprDetails: TPRDetailsResolver },
      },
      {
        title: 'PAT report details',
        path: 'pat-details',
        loadComponent: () => import('./pat/pat-details/pat-details.component').then((c) => c.PatDetailsComponent),
      },
      {
        title: 'Entry details',
        path: 'pat-details/:entryId',
        loadComponent: () =>
          import('./pat/pat-details/entry-details/entry-details.component').then((c) => c.EntryDetailsComponent),
        data: { breadcrumb: false, backlink: '..' },
      },
      {
        title: 'Products',
        path: 'products',
        loadComponent: () =>
          import('./target-period/tpr-details/products/tpr-products.component').then((c) => c.TprProductsComponent),
        data: { breadcrumb: { text: 'TP report results', from: ':targetPeriodYear' } },
        resolve: { tprDetails: TPRDetailsResolver },
      },
      {
        title: 'Facility target period locking',
        path: 'toggle-lock',
        loadComponent: () =>
          import('./target-period/toggle-lock/toggle-lock.component').then((c) => c.ToggleLockComponent),
        canActivate: [toggleLockGuard],
        data: { breadcrumb: false, backlink: { link: '../../../', fragment: 'reports' } },
      },
      {
        title: 'Variation submission',
        path: 'variation-submission',
        loadComponent: () =>
          import('./target-period/variation-submission/variation-submission.component').then(
            (c) => c.VariationSubmissionComponent,
          ),
        canActivate: [variationSubmissionGuard],
        data: { breadcrumb: false, backlink: { link: '../../../', fragment: 'reports' } },
      },
    ],
  },
];

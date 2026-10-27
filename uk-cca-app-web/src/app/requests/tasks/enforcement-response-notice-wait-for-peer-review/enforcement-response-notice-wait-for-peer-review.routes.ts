import { Routes } from '@angular/router';

export const ENFORCEMENT_RESPONSE_NOTICE_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Enforcement response notice sent for peer review',
    path: '',
    children: [
      {
        title: 'Enforcement response notice',
        path: 'upload-enforcement-response-notice',
        loadComponent: () => import('@requests/common').then((c) => c.UploadEnforcementResponseNoticeSummaryComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: '**',
        redirectTo: 'upload-enforcement-response-notice',
      },
    ],
  },
];

import { Routes } from '@angular/router';

export const ENFORCEMENT_RESPONSE_NOTICE_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'upload-enforcement-response-notice',
        title: 'Enforcement response notice',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.UploadEnforcementResponseNoticeSummaryComponent),
      },
      {
        path: '**',
        redirectTo: 'upload-enforcement-response-notice',
      },
    ],
  },
];

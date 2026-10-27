import { Routes } from '@angular/router';

import {
  enforcementTypeGuard,
  uploadEnforcementResponseNoticeRedirectGuard,
} from './upload-enforcement-response-notice.guard';

export const UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [uploadEnforcementResponseNoticeRedirectGuard],
        children: [],
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'What type of enforcement response notice would you like to send?',
        path: 'enforcement-type',
        loadComponent: () => import('./enforcement-type/enforcement-type.component'),
        canActivate: [enforcementTypeGuard],
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Upload enforcement response notice',
        path: 'upload-notice',
        loadComponent: () => import('./upload-notice/upload-notice.component'),
        data: { backlink: '../enforcement-type', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-your-answers/check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];

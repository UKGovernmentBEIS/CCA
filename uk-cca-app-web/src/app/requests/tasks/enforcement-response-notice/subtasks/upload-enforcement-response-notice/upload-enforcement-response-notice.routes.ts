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
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/summary.component'),
      },
      {
        path: 'enforcement-type',
        title: 'What type of enforcement response notice would you like to send?',
        canActivate: [enforcementTypeGuard],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./enforcement-type/enforcement-type.component'),
      },
      {
        path: 'upload-notice',
        title: 'Upload enforcement response notice',
        data: { backlink: '../enforcement-type', breadcrumb: false },
        loadComponent: () => import('./upload-notice/upload-notice.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/check-your-answers.component'),
      },
    ],
  },
];

import { Routes } from '@angular/router';

import { auditDetailsRedirectGuard } from './audit-details-corrective-actions.guard';

export const AUDIT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [auditDetailsRedirectGuard],
        children: [],
      },
      {
        title: 'Details of the audit',
        path: 'details',
        loadComponent: () => import('./details/audit-details.component').then((c) => c.AuditDetailsComponent),
        data: { breadcrumb: false, backlink: '../../../' },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/audit-details-check-your-answers.component').then(
            (c) => c.AuditDetailsCheckYourAnswersComponent,
          ),
        data: { breadcrumb: false, backlink: '../details' },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/audit-details-summary.component').then((c) => c.AuditDetailsSummaryComponent),
        data: { breadcrumb: false, backlink: '../../../' },
      },
    ],
  },
];

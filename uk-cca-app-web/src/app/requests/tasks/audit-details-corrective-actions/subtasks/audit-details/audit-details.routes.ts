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
        path: 'details',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () => import('./details/audit-details.component').then((c) => c.AuditDetailsComponent),
      },
      {
        path: 'check-your-answers',
        data: { breadcrumb: false, backlink: '../details' },
        loadComponent: () =>
          import('./check-your-answers/audit-details-check-your-answers.component').then(
            (c) => c.AuditDetailsCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./summary/audit-details-summary.component').then((c) => c.AuditDetailsSummaryComponent),
      },
    ],
  },
];

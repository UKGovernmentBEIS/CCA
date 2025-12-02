import { Routes } from '@angular/router';

export const FACILITY_AUDIT_DETAILS_CORRECTIVE_ACTIONS_COMPLETED_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'details',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () =>
          import('./details/details-corrective-actions-details.component').then(
            (c) => c.DetailsCorrectiveActionsDetailsComponent,
          ),
      },
      {
        path: 'actions',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () =>
          import('./actions/details-corrective-actions-actions.component').then(
            (c) => c.DetailsCorrectiveActionsActionsComponent,
          ),
      },
    ],
  },
];

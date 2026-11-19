import { Routes } from '@angular/router';

export const FACILITY_AUDIT_DETAILS_CORRECTIVE_ACTIONS_COMPLETED_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'details',
        loadComponent: () =>
          import('./details/details-corrective-actions-details.component').then(
            (c) => c.DetailsCorrectiveActionsDetailsComponent,
          ),
        data: { breadcrumb: false, backlink: '../../' },
      },
      {
        path: 'actions',
        loadComponent: () =>
          import('./actions/details-corrective-actions-actions.component').then(
            (c) => c.DetailsCorrectiveActionsActionsComponent,
          ),
        data: { breadcrumb: false, backlink: '../../' },
      },
    ],
  },
];

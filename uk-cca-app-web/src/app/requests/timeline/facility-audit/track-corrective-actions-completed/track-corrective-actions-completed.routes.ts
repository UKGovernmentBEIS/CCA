import { Routes } from '@angular/router';

export const FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_COMPLETED_ROUTES: Routes = [
  {
    path: ':actionId',
    loadComponent: () =>
      import('./details/track-corrective-actions-completed-details.component').then(
        (c) => c.TrackCorrectiveActionsCompletedDetailsComponent,
      ),
    data: { breadcrumb: false, backlink: '../../' },
  },
];

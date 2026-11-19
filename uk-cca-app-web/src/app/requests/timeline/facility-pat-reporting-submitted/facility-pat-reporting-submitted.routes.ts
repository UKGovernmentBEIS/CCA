import { Routes } from '@angular/router';

export const FACILITY_PAT_REPORTING_SUBMITTED_ROUTES: Routes = [
  {
    path: ':actionId',
    loadComponent: () =>
      import('./details/facility-pat-reporting-submitted-details.component').then(
        (c) => c.FacilityPATReportingSubmittedDetailsComponent,
      ),
    data: { breadcrumb: false, backlink: '../../' },
  },
];

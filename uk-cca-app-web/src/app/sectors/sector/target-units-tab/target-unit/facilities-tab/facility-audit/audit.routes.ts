import { Routes } from '@angular/router';

import { FacilityDetailsResolver } from '../facility-details.resolver';
import { initFacilityAuditGuard } from './audit-init.guard';

export const FACILITY_AUDIT_ROUTES: Routes = [
  {
    path: '',
    resolve: { facilityDetails: FacilityDetailsResolver },
    canActivate: [initFacilityAuditGuard],
    children: [
      {
        path: '',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('./audit/audit.component').then((c) => c.AuditComponent),
      },
      {
        path: 'reasons',
        data: { backlink: '..', breadcrumb: false },
        loadComponent: () => import('./reasons/reasons.component').then((c) => c.ReasonsComponent),
      },
    ],
  },
];

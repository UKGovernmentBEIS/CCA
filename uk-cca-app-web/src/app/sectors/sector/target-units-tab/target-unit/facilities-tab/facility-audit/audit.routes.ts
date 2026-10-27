import { Routes } from '@angular/router';

import { RouteBacklink } from '@netz/common/navigation';

import { FacilityDetailsResolver } from '../facility-details.resolver';
import { initFacilityAuditGuard } from './audit-init.guard';

export const FACILITY_AUDIT_ROUTES: Routes = [
  {
    path: '',
    canActivate: [initFacilityAuditGuard],
    resolve: { facilityDetails: FacilityDetailsResolver },
    children: [
      {
        title: 'Facility audit',
        path: '',
        loadComponent: () => import('./audit/audit.component').then((c) => c.AuditComponent),
        data: { backlink: { link: '../', fragment: 'audit' }, breadcrumb: false },
      },
      {
        title: 'Why should this facility be audited?',
        path: 'reasons',
        loadComponent: () => import('./reasons/reasons.component').then((c) => c.ReasonsComponent),
        data: {
          // Entered from the audit tab's change links (`?change=true`) or from the audit form
          backlink: ((_data, route) =>
            route.queryParamMap.get('change') === 'true'
              ? { link: '../..', fragment: 'audit' }
              : '..') satisfies RouteBacklink,
          breadcrumb: false,
        },
      },
    ],
  },
];

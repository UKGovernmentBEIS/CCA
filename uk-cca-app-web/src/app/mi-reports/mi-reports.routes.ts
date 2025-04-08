import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { miReportTypeDescriptionMap } from './core/mi-report';
import { miReportsListGuard } from './core/mi-reports-list.guard';
import { MiReportsComponent } from './mi-reports.component';

export const MI_REPORTS_ROUTES: Routes = [
  {
    path: '',
    data: { pageTitle: 'MI Reports' },
    component: MiReportsComponent,
    canDeactivate: [PendingRequestGuard],
    canActivate: [miReportsListGuard],
  },
  {
    path: 'custom',
    data: {
      pageTitle: miReportTypeDescriptionMap['CUSTOM'],
      breadcrumb: miReportTypeDescriptionMap['CUSTOM'],
    },
    loadComponent: () => import('./custom/custom.component').then((c) => c.CustomReportComponent),
  },
];

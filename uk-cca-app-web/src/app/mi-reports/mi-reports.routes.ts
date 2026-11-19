import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { MiReportsUserDefinedService } from 'cca-api';

export const MI_REPORTS_ROUTES: Routes = [
  {
    title: 'MI Reports',
    path: '',
    loadComponent: () => import('./mi-reports.component').then((c) => c.MiReportsComponent),
    canDeactivate: [PendingRequestGuard],
  },
  {
    title: 'Create custom report',
    path: 'custom',
    loadComponent: () => import('./custom/custom.component').then((c) => c.CustomReportComponent),
    data: {
      backlink: '../',
      breadcrumb: false,
    },
  },
  {
    title: 'Create new report',
    path: 'create-mi-report',
    loadComponent: () => import('./mi-report-form/mi-report-form.component').then((c) => c.MiReportFormComponent),
    data: {
      backlink: '../',
      breadcrumb: false,
    },
  },
  {
    title: 'Edit MI report',
    path: 'edit-mi-report/:queryId',
    loadComponent: () => import('./mi-report-form/mi-report-form.component').then((c) => c.MiReportFormComponent),
    data: {
      backlink: '../../',
      breadcrumb: false,
    },
    resolve: {
      query: (route: ActivatedRouteSnapshot) =>
        inject(MiReportsUserDefinedService).getMiReportUserDefinedById(+route.paramMap.get('queryId')),
    },
  },
  {
    title: 'Edit MI report',
    path: 'delete-mi-report/:queryId',
    loadComponent: () => import('./delete-mi-report/delete-mi-report.component').then((c) => c.DeleteMiReportComponent),
    data: {
      backlink: '../../',
      breadcrumb: false,
    },
    resolve: {
      query: (route: ActivatedRouteSnapshot) =>
        inject(MiReportsUserDefinedService).getMiReportUserDefinedById(+route.paramMap.get('queryId')),
    },
  },
];

import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { MiReportsUserDefinedService } from 'cca-api';

import { MiReportsComponent } from './mi-reports.component';

export const MI_REPORTS_ROUTES: Routes = [
  {
    path: '',
    title: 'MI Reports',
    component: MiReportsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'custom',
    title: 'Create custom report',
    data: {
      backlink: '../',
      breadcrumb: false,
    },
    loadComponent: () => import('./custom/custom.component').then((c) => c.CustomReportComponent),
  },
  {
    path: 'create-mi-report',
    title: 'Create new report',
    data: {
      backlink: '../',
      breadcrumb: false,
    },
    loadComponent: () => import('./mi-report-form/mi-report-form.component').then((c) => c.MiReportFormComponent),
  },
  {
    path: 'edit-mi-report/:queryId',
    title: 'Edit MI report',
    data: {
      backlink: '../../',
      breadcrumb: false,
    },
    resolve: {
      query: (route: any) =>
        inject(MiReportsUserDefinedService).getMiReportUserDefinedById(+route.paramMap.get('queryId')),
    },
    loadComponent: () => import('./mi-report-form/mi-report-form.component').then((c) => c.MiReportFormComponent),
  },
  {
    path: 'delete-mi-report/:queryId',
    title: 'Edit MI report',
    data: {
      backlink: '../../',
      breadcrumb: false,
    },
    resolve: {
      query: (route: any) =>
        inject(MiReportsUserDefinedService).getMiReportUserDefinedById(+route.paramMap.get('queryId')),
    },
    loadComponent: () => import('./delete-mi-report/delete-mi-report.component').then((c) => c.DeleteMiReportComponent),
  },
];

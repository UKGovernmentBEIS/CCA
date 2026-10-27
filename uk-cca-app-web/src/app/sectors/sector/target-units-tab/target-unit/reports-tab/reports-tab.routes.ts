import { targetUnitTabBreadcrumb } from '../../target-unit-breadcrumb';
import { reportSubmittedGuardPAT } from './pat/report-submitted/report-submitted.guard';
import { reportSubmittedGuard } from './performance-data/report-submitted/report-submitted.guard';
import { toggleLockGuard } from './performance-data/toggle-lock/toggle-lock.guard';

const reportsTabBreadcrumbData = { breadcrumb: targetUnitTabBreadcrumb('reports') };

export const REPORTS_TAB_ROUTES = [
  {
    title: 'Do you want to lock the performance reporting task?',
    path: ':targetPeriodType/toggle-lock',
    loadComponent: () =>
      import('./performance-data/toggle-lock/toggle-lock.component').then((c) => c.ToggleLockComponent),
    canActivate: [toggleLockGuard],
    data: { breadcrumb: false, backlink: { link: '../../', fragment: 'reports' } },
  },
  {
    title: 'Report submitted',
    path: ':targetPeriodType/:reportType/performance-data-submitted-report',
    loadComponent: () =>
      import('./performance-data/report-submitted/report-submitted.component').then((c) => c.ReportSubmittedComponent),
    canActivate: [reportSubmittedGuard],
    data: reportsTabBreadcrumbData,
  },
  {
    title: 'Report submitted',
    path: ':targetPeriodType/:reportType/pat-submitted-report',
    loadComponent: () =>
      import('./pat/report-submitted/report-submitted.component').then((c) => c.ReportSubmittedComponent),
    canActivate: [reportSubmittedGuardPAT],
    data: reportsTabBreadcrumbData,
  },
  {
    title: 'Your download has started',
    path: ':targetPeriodType/:reportType/file-download/:uuid',
    loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
  },
];

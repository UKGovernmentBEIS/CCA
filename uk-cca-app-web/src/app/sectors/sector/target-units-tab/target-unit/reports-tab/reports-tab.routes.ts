import { reportSubmittedGuardPAT } from './pat/report-submitted/report-submitted.guard';
import { reportSubmittedGuard } from './performance-data/report-submitted/report-submitted.guard';
import { toggleLockGuard } from './performance-data/toggle-lock/toggle-lock.guard';

export const REPORTS_TAB_ROUTES = [
  {
    canActivate: [toggleLockGuard],
    path: ':targetPeriodType/toggle-lock',
    title: 'Do you want to lock the performance reporting task?',
    data: { breadcrumb: false, backlink: '../../' },
    loadComponent: () =>
      import('./performance-data/toggle-lock/toggle-lock.component').then((c) => c.ToggleLockComponent),
  },
  {
    canActivate: [reportSubmittedGuard],
    path: ':targetPeriodType/:reportType/performance-data-submitted-report',
    title: 'Report submitted',
    loadComponent: () =>
      import('./performance-data/report-submitted/report-submitted.component').then((c) => c.ReportSubmittedComponent),
  },
  {
    canActivate: [reportSubmittedGuardPAT],
    path: ':targetPeriodType/:reportType/pat-submitted-report',
    title: 'Report submitted',
    loadComponent: () =>
      import('./pat/report-submitted/report-submitted.component').then((c) => c.ReportSubmittedComponent),
  },
  {
    path: ':targetPeriodType/:reportType/file-download/:uuid',
    title: 'Your download has started',
    loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
  },
];

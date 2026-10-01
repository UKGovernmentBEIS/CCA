import { Routes } from '@angular/router';

export const MARK_FACILITIES_ROUTES: Routes = [
  {
    path: 'all-paid',
    title: 'Are you sure you want to mark all the facilities of this target unit as Paid?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-all-paid/mark-all-paid.component').then((c) => c.MarkAllPaidComponent),
  },
  {
    path: 'paid',
    title: 'Are you sure you want to mark this facility as Paid?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-paid/mark-paid.component').then((c) => c.MarkPaidComponent),
  },
  {
    path: 'in-progress',
    title: 'Are you sure you want to mark this facility as In progress?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-in-progress/mark-in-progress.component').then((c) => c.MarkInProgressComponent),
  },
  {
    path: 'cancelled',
    title: 'Are you sure you want to mark this facility as Cancelled for this subsistence fees payment request?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-cancelled/mark-cancelled.component').then((c) => c.MarkCancelledComponent),
  },
  {
    path: 'confirmation/:type',
    title: 'The selected facilities have been marked',
    data: { breadcrumb: false, backlink: false },
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
  {
    path: 'history/:moaFacilityId',
    title: 'Marking history',
    data: { breadcrumb: false, backlink: '../../..' },
    loadComponent: () => import('./marking-history/marking-history.component').then((c) => c.MarkingHistoryComponent),
  },
];

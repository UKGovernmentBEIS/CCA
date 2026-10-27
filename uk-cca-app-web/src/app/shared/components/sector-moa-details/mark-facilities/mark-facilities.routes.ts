import { Routes } from '@angular/router';

export const MARK_FACILITIES_ROUTES: Routes = [
  {
    title: 'Are you sure you want to mark all target units of this sector and all their facilities as Paid?',
    path: 'all-paid',
    loadComponent: () => import('./mark-all-paid/mark-all-paid.component').then((c) => c.MarkAllPaidComponent),
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Are you sure you want to mark this target unit and all its facilities as Paid?',
    path: 'paid',
    loadComponent: () => import('./mark-paid/mark-paid.component').then((c) => c.MarkPaidComponent),
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Are you sure you want to mark this target unit and all its facilities as In progress?',
    path: 'in-progress',
    loadComponent: () => import('./mark-in-progress/mark-in-progress.component').then((c) => c.MarkInProgressComponent),
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title:
      'Are you sure you want to mark this target unit and all its facilities as Cancelled for this subsistence fees payment request?',
    path: 'cancelled',
    loadComponent: () => import('./mark-cancelled/mark-cancelled.component').then((c) => c.MarkCancelledComponent),
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'The selected facilities have been marked',
    path: 'confirmation/:type',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
    data: { breadcrumb: false, backlink: false },
  },
];

import { Routes } from '@angular/router';

export const MARK_FACILITIES_ROUTES: Routes = [
  {
    path: 'all-paid',
    title: 'Are you sure you want to mark all target units of this sector and all their facilities as Paid?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-all-paid/mark-all-paid.component').then((c) => c.MarkAllPaidComponent),
  },
  {
    path: 'paid',
    title: 'Are you sure you want to mark this target unit and all its facilities as Paid?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-paid/mark-paid.component').then((c) => c.MarkPaidComponent),
  },
  {
    path: 'in-progress',
    title: 'Are you sure you want to mark this target unit and all its facilities as In progress?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-in-progress/mark-in-progress.component').then((c) => c.MarkInProgressComponent),
  },
  {
    path: 'cancelled',
    title:
      'Are you sure you want to mark this target unit and all its facilities as Cancelled for this subsistence fees payment request?',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-cancelled/mark-cancelled.component').then((c) => c.MarkCancelledComponent),
  },
  {
    path: 'confirmation/:type',
    title: 'The selected facilities have been marked',
    data: { breadcrumb: false, backlink: false },
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
];

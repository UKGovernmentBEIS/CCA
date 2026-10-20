import { Routes } from '@angular/router';

export const MARK_FACILITIES_ROUTES: Routes = [
  {
    path: 'all-paid',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-all-paid/mark-all-paid.component').then((c) => c.MarkAllPaidComponent),
  },
  {
    path: 'paid',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-paid/mark-paid.component').then((c) => c.MarkPaidComponent),
  },
  {
    path: 'in-progress',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-in-progress/mark-in-progress.component').then((c) => c.MarkInProgressComponent),
  },
  {
    path: 'cancelled',
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./mark-cancelled/mark-cancelled.component').then((c) => c.MarkCancelledComponent),
  },
  {
    path: 'confirmation/:type',
    data: { breadcrumb: false, backlink: false },
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
];

import { Routes } from '@angular/router';

import { AuthGuard, PendingRequestGuard } from '@shared/guards';

export const TWO_FA_ROUTES: Routes = [
  {
    title: 'Request to change two factor authentication',
    path: 'change',
    loadComponent: () => import('./change-2fa/change-2fa.component').then((c) => c.Change2faComponent),
    canActivate: [AuthGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    title: 'Request to change two factor authentication',
    path: 'request-change',
    loadComponent: () => import('./delete-2fa/delete-2fa.component').then((c) => c.Delete2faComponent),
  },
  {
    title: 'This link is invalid',
    path: 'invalid-link',
    loadComponent: () =>
      import('../invitation/invalid-link/invalid-link.component').then((c) => c.InvalidLinkComponent),
  },
  {
    title: 'Request two factor authentication reset',
    path: 'request-2fa-reset',
    loadComponent: () =>
      import('./request-two-fa-reset/request-two-fa-reset.component').then((c) => c.RequestTwoFaResetComponent),
  },
  {
    title: 'Reset two factor authentication',
    path: 'reset-2fa',
    loadComponent: () => import('./reset-two-fa/reset-two-fa.component').then((c) => c.ResetTwoFaComponent),
    canActivate: [AuthGuard],
  },
];

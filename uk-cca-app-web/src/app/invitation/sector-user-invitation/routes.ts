import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import {
  resetSectorInvitationStore,
  SectorUserInvitationGuard,
  SectorUserNoTokenGuard,
} from './sector-user-invitation.guard';
import { SectorUserInvitationStore } from './sector-user-invitation.store';

export const SECTOR_USER_INVITATION_ROUTES: Routes = [
  {
    path: 'sector-user',
    data: { blockSignInRedirect: true },
    children: [
      {
        title: 'Enter your details',
        path: '',
        loadComponent: () => import('./sector-user-invitation.component').then((c) => c.SectorUserInvitationComponent),
        canActivate: [SectorUserInvitationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        title: 'Create a password',
        path: 'create-password',
        loadComponent: () =>
          import('./sector-user-invitation-create-password/sector-user-invitation-create-password.component').then(
            (c) => c.SectorUserInvitationCreatePasswordComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
        data: { backlink: '../' },
      },
      {
        title: 'Create a password',
        path: 'set-password-only',
        loadComponent: () =>
          import('./sector-user-invitation-password-only/sector-user-invitation-password-only.component').then(
            (c) => c.SectorUserInvitationPasswordOnlyComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
      },
      {
        title: 'Check your answers',
        path: 'summary',
        loadComponent: () =>
          import('./sector-user-invitation-summary/sector-user-invitation-summary.component').then(
            (c) => c.SectorUserInvitationSummaryComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
        data: { backlink: '../create-password' },
      },
      {
        title: "You've successfully activated your user account",
        path: 'confirmed',
        loadComponent: () =>
          import('../invitation-confirmation/invitation-confirmation.component').then(
            (c) => c.InvitationConfirmationComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
        canDeactivate: [resetSectorInvitationStore],
      },
      {
        title: 'You are successfully added to a sector account',
        path: 'confirmed-existing',
        loadComponent: () =>
          import('../invitation-existing-confirmation/invitation-existing-confirmation.component').then(
            (c) => c.InvitationExistingConfirmationComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
      },
      {
        title: 'This link is invalid/expired',
        path: 'invalid-link',
        loadComponent: () => import('../invalid-link/invalid-link.component').then((c) => c.InvalidLinkComponent),
      },
    ],
    providers: [SectorUserInvitationStore],
  },
];

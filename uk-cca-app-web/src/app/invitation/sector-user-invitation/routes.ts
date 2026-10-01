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
    providers: [SectorUserInvitationStore],
    children: [
      {
        path: '',
        title: 'Enter your details',
        loadComponent: () => import('./sector-user-invitation.component').then((c) => c.SectorUserInvitationComponent),
        canActivate: [SectorUserInvitationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'create-password',
        title: 'Create a password',
        data: { backlink: '../' },
        loadComponent: () =>
          import('./sector-user-invitation-create-password/sector-user-invitation-create-password.component').then(
            (c) => c.SectorUserInvitationCreatePasswordComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
      },
      {
        path: 'set-password-only',
        title: 'Create a password',
        loadComponent: () =>
          import('./sector-user-invitation-password-only/sector-user-invitation-password-only.component').then(
            (c) => c.SectorUserInvitationPasswordOnlyComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
      },
      {
        path: 'summary',
        title: 'Check your answers',
        data: { backlink: '../create-password' },
        loadComponent: () =>
          import('./sector-user-invitation-summary/sector-user-invitation-summary.component').then(
            (c) => c.SectorUserInvitationSummaryComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
      },
      {
        path: 'confirmed',
        title: "You've successfully activated your user account",
        loadComponent: () =>
          import('../invitation-confirmation/invitation-confirmation.component').then(
            (c) => c.InvitationConfirmationComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
        canDeactivate: [resetSectorInvitationStore],
      },
      {
        path: 'confirmed-existing',
        title: 'You are successfully added to a sector account',
        loadComponent: () =>
          import('../invitation-existing-confirmation/invitation-existing-confirmation.component').then(
            (c) => c.InvitationExistingConfirmationComponent,
          ),
        canActivate: [SectorUserNoTokenGuard],
      },
      {
        path: 'invalid-link',
        title: 'This link is invalid/expired',
        loadComponent: () => import('../invalid-link/invalid-link.component').then((c) => c.InvalidLinkComponent),
      },
    ],
  },
];

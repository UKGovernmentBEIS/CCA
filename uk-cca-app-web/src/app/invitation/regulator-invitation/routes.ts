import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';
import { RegulatorInvitationGuard } from './regulator-invitation.guard';

export const REGULATOR_INVITATION_ROUTES: Routes = [
  {
    path: 'regulator',
    data: { blockSignInRedirect: true },
    children: [
      {
        title: 'Activate your account',
        path: '',
        loadComponent: () => import('./regulator-invitation.component').then((c) => c.RegulatorInvitationComponent),
        canActivate: [RegulatorInvitationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        title: "You've successfully activated your user account",
        path: 'confirmed',
        loadComponent: () =>
          import('../invitation-confirmation/invitation-confirmation.component').then(
            (c) => c.InvitationConfirmationComponent,
          ),
      },
      {
        title: 'This link is invalid/expired',
        path: 'invalid-link',
        loadComponent: () => import('../invalid-link/invalid-link.component').then((c) => c.InvalidLinkComponent),
      },
    ],
    providers: [InvitedRegulatorUserStore],
  },
];

import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';
import { RegulatorInvitationGuard } from './regulator-invitation.guard';

export const REGULATOR_INVITATION_ROUTES: Routes = [
  {
    path: 'regulator',
    data: { blockSignInRedirect: true },
    providers: [InvitedRegulatorUserStore],
    children: [
      {
        path: '',
        title: 'Activate your account',
        loadComponent: () => import('./regulator-invitation.component').then((c) => c.RegulatorInvitationComponent),
        canActivate: [RegulatorInvitationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmed',
        title: "You've successfully activated your user account",
        loadComponent: () =>
          import('../invitation-confirmation/invitation-confirmation.component').then(
            (c) => c.InvitationConfirmationComponent,
          ),
      },
      {
        path: 'invalid-link',
        title: 'This link is invalid/expired',
        loadComponent: () => import('../invalid-link/invalid-link.component').then((c) => c.InvalidLinkComponent),
      },
    ],
  },
];

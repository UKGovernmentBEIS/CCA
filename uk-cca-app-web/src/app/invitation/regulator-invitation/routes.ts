import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';
import { RegulatorInvitationGuard } from './regulator-invitation.guard';

export const REGULATOR_INVITATION_ROUTES: Routes = [
  {
    path: 'regulator',
    data: { blockSignInRedirect: true, pageTitle: 'Activate your account' },
    providers: [InvitedRegulatorUserStore],
    children: [
      {
        path: '',
        loadComponent: () => import('./regulator-invitation.component').then((c) => c.RegulatorInvitationComponent),
        canActivate: [RegulatorInvitationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmed',
        data: { pageTitle: "You've successfully activated your user account" },
        loadComponent: () =>
          import('../invitation-confirmation/invitation-confirmation.component').then(
            (c) => c.InvitationConfirmationComponent,
          ),
      },
      {
        path: 'invalid-link',
        data: { pageTitle: 'This link is invalid/expired' },
        loadComponent: () => import('../invalid-link/invalid-link.component').then((c) => c.InvalidLinkComponent),
      },
    ],
  },
];

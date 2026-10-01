import { Routes } from '@angular/router';

import { OperatorUserInvitationFormProvider } from './form.provider';
import {
  OperatorUserInvitationGuard,
  OperatorUserNoTokenGuard,
  resetOperatorInvitationStore,
} from './operator-invitation.guard';
import { OperatorUserInvitationStore } from './store';

export const OPERATOR_INVITATION_USER_ROUTES: Routes = [
  {
    path: '',
    providers: [OperatorUserInvitationFormProvider, OperatorUserInvitationStore],
    canDeactivate: [resetOperatorInvitationStore],
    children: [
      {
        path: '',
        title: 'Enter your details',
        loadComponent: () =>
          import('./details/operator-user-invitation-details.component').then((c) => c.OperatorUserInvitationComponent),
        canActivate: [OperatorUserInvitationGuard],
      },
      {
        path: 'create-password',
        title: 'Create a password',
        data: { backlink: '../' },
        canActivate: [OperatorUserNoTokenGuard],
        loadComponent: () =>
          import('./create-password/operator-user-create-password.component').then(
            (c) => c.OperatorUserCreatePasswordComponent,
          ),
      },
      {
        path: 'set-password-only',
        title: 'Create a password',
        canActivate: [OperatorUserNoTokenGuard],
        loadComponent: () =>
          import('./set-password-only/set-password-only.component').then((c) => c.SetPasswordOnlyComponent),
      },
      {
        path: 'summary',
        title: 'Check your answers',
        data: { backlink: '../create-password' },
        canActivate: [OperatorUserNoTokenGuard],
        loadComponent: () =>
          import('./summary/operator-user-invitation-summary.component').then(
            (c) => c.OperatorUserInvitationSummaryComponent,
          ),
      },
      {
        path: 'confirmed',
        title: "You've successfully activated your user account",
        canActivate: [OperatorUserNoTokenGuard],
        loadComponent: () =>
          import('../invitation-confirmation/invitation-confirmation.component').then(
            (c) => c.InvitationConfirmationComponent,
          ),
      },
      {
        path: 'confirmed-existing',
        title: 'You are successfully added to a target unit account',
        canActivate: [OperatorUserNoTokenGuard],
        loadComponent: () =>
          import('./invitation-existing-confirmation/invitation-existing-confirmation.component').then(
            (c) => c.InvitationExistingConfirmationComponent,
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

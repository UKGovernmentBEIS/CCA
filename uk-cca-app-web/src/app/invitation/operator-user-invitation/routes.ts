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
    canDeactivate: [resetOperatorInvitationStore],
    children: [
      {
        title: 'Enter your details',
        path: '',
        loadComponent: () =>
          import('./details/operator-user-invitation-details.component').then((c) => c.OperatorUserInvitationComponent),
        canActivate: [OperatorUserInvitationGuard],
      },
      {
        title: 'Create a password',
        path: 'create-password',
        loadComponent: () =>
          import('./create-password/operator-user-create-password.component').then(
            (c) => c.OperatorUserCreatePasswordComponent,
          ),
        canActivate: [OperatorUserNoTokenGuard],
        data: { backlink: '../' },
      },
      {
        title: 'Create a password',
        path: 'set-password-only',
        loadComponent: () =>
          import('./set-password-only/set-password-only.component').then((c) => c.SetPasswordOnlyComponent),
        canActivate: [OperatorUserNoTokenGuard],
      },
      {
        title: 'Check your answers',
        path: 'summary',
        loadComponent: () =>
          import('./summary/operator-user-invitation-summary.component').then(
            (c) => c.OperatorUserInvitationSummaryComponent,
          ),
        canActivate: [OperatorUserNoTokenGuard],
        data: { backlink: '../create-password' },
      },
      {
        title: "You've successfully activated your user account",
        path: 'confirmed',
        loadComponent: () =>
          import('../invitation-confirmation/invitation-confirmation.component').then(
            (c) => c.InvitationConfirmationComponent,
          ),
        canActivate: [OperatorUserNoTokenGuard],
      },
      {
        title: 'You are successfully added to a target unit account',
        path: 'confirmed-existing',
        loadComponent: () =>
          import('./invitation-existing-confirmation/invitation-existing-confirmation.component').then(
            (c) => c.InvitationExistingConfirmationComponent,
          ),
        canActivate: [OperatorUserNoTokenGuard],
      },
      {
        title: 'This link is invalid/expired',
        path: 'invalid-link',
        loadComponent: () => import('../invalid-link/invalid-link.component').then((c) => c.InvalidLinkComponent),
      },
    ],
    providers: [OperatorUserInvitationFormProvider, OperatorUserInvitationStore],
  },
];

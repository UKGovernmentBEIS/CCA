import { Routes } from '@angular/router';

export const FORGOT_PASSWORD_ROUTES: Routes = [
  {
    title: 'Forgot password',
    path: '',
    loadComponent: () => import('./submit-email/submit-email.component').then((c) => c.SubmitEmailComponent),
  },
  {
    title: 'This link is invalid',
    path: 'invalid-link',
    loadComponent: () =>
      import('./email-link-invalid/email-link-invalid.component').then((c) => c.EmailLinkInvalidComponent),
  },
  {
    title: 'Reset password',
    path: 'reset-password',
    loadComponent: () => import('./reset-password/reset-password.component').then((c) => c.ResetPasswordComponent),
  },
  {
    title: 'Submit otp',
    path: 'otp',
    loadComponent: () => import('./submit-otp/submit-otp.component').then((c) => c.SubmitOtpComponent),
  },
];

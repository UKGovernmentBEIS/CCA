import { Routes } from '@angular/router';

import { EmailLinkInvalidComponent } from './email-link-invalid/email-link-invalid.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { SubmitEmailComponent } from './submit-email/submit-email.component';
import { SubmitOtpComponent } from './submit-otp/submit-otp.component';

export const forgotPasswordRoutes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Forgot password', breadcrumb: true },
    component: SubmitEmailComponent,
  },
  {
    path: 'invalid-link',
    data: { pageTitle: 'This link is invalid', breadcrumb: true },
    component: EmailLinkInvalidComponent,
  },
  {
    path: 'reset-password',
    data: { pageTitle: 'Reset password', breadcrumb: true },
    component: ResetPasswordComponent,
  },
  {
    path: 'otp',
    data: { pageTitle: 'Submit otp', breadcrumb: true },
    component: SubmitOtpComponent,
  },
];

import { ExtraOptions, Routes } from '@angular/router';

import { AuthorizeGuard } from '@core/guards/authorize.guard';
import { LoggedInGuard } from '@core/guards/logged-in.guard';
import { NonAuthGuard } from '@core/guards/non-auth.guard';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { TermsAndConditionsGuard } from '@core/guards/terms-and-conditions.guard';

import { AccessibilityComponent } from './accessibility/accessibility.component';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { DashboardPageComponent } from './dashboard/containers/dashboard-page/dashboard-page.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LandingPageGuard } from './landing-page/landing-page.guard';
import { LegislationComponent } from './legislation/legislation.component';
import { PrivacyNoticeComponent } from './privacy-notice/privacy-notice.component';
import { TermsAndConditionsComponent } from './terms-and-conditions/terms-and-conditions.component';
import { TimedOutComponent } from './timeout/timed-out/timed-out.component';
import { VersionComponent } from './version/version.component';

export const APP_ROUTES: Routes = [
  {
    path: 'landing',
    data: { pageTitle: 'CCA', breadcrumb: 'Home' },
    component: LandingPageComponent,
    canActivate: [LandingPageGuard],
  },
  {
    path: '',
    redirectTo: 'landing',
    pathMatch: 'full',
  },
  {
    path: '',
    data: { breadcrumb: 'Home' },
    children: [
      {
        path: 'about',
        data: { pageTitle: 'About', breadcrumb: true },
        component: VersionComponent,
      },
      {
        path: 'privacy-notice',
        data: { pageTitle: 'Privacy notice', breadcrumb: true },
        component: PrivacyNoticeComponent,
      },
      {
        path: 'accessibility',
        data: { pageTitle: 'Accessibility Statement', breadcrumb: true },
        component: AccessibilityComponent,
      },
      {
        path: 'contact-us',
        data: { pageTitle: 'Contact us', breadcrumb: true },
        component: ContactUsComponent,
      },
      {
        path: 'legislation',
        data: { pageTitle: 'UK ETS legislation', breadcrumb: true },
        component: LegislationComponent,
      },
      {
        path: 'feedback',
        data: { pageTitle: 'Feedback', breadcrumb: true },
        component: FeedbackComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'forgot-password',
        loadChildren: () => import('./forgot-password/forgot-password.routes').then((r) => r.FORGOT_PASSWORD_ROUTES),
      },
      {
        path: '2fa',
        loadChildren: () => import('./two-fa/two-fa.routes').then((r) => r.TWO_FA_ROUTES),
      },
    ],
  },
  {
    path: 'error',
    loadChildren: () => import('./error/error.routes').then((r) => r.ERROR_ROUTES),
  },
  {
    path: 'invitation',
    loadChildren: () => import('./invitation/invitation.routes').then((r) => r.INVITATION_ROUTES),
  },

  {
    path: 'timed-out',
    data: { pageTitle: 'Session Timeout' },
    canActivate: [NonAuthGuard],
    component: TimedOutComponent,
  },
  {
    path: '',
    canActivate: [LoggedInGuard],
    children: [
      {
        path: '',
        data: { breadcrumb: 'Dashboard' },
        children: [
          {
            path: 'dashboard',
            data: { pageTitle: 'Climate Change Agreement dashboard' },
            canActivate: [AuthorizeGuard],
            component: DashboardPageComponent,
          },
          {
            path: 'user',
            canActivate: [AuthorizeGuard],
            children: [
              {
                path: 'regulators',
                data: { breadcrumb: 'Regulator users' },
                loadChildren: () => import('./regulators/regulators.routes').then((r) => r.REGULATOR_ROUTES),
              },
            ],
          },
          {
            path: 'sectors',
            data: { breadcrumb: 'Manage sectors' },
            loadChildren: () => import('./sectors/sectors.routes').then((r) => r.SECTORS_ROUTES),
          },
          {
            path: 'target-unit-accounts',
            data: { breadcrumb: 'Target unit accounts' },
            loadChildren: () => import('./accounts/accounts.routes').then((r) => r.ACCOUNT_ROUTES),
          },
          {
            path: 'tasks',
            canActivate: [AuthorizeGuard],
            loadChildren: () => import('./requests/tasks/tasks.routes').then((r) => r.TASKS_ROUTES),
          },
          {
            path: 'terms',
            data: { pageTitle: 'Accept terms and conditions' },
            component: TermsAndConditionsComponent,
            canActivate: [TermsAndConditionsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'registration',
    children: [
      {
        path: 'invitation',
        loadChildren: () =>
          import('./invitation/operator-user-invitation/routes').then((r) => r.OPERATOR_INVITATION_USER_ROUTES),
      },
    ],
  },
  // The route below handles all unknown routes / Page Not Found functionality.
  // Please keep this route last else there will be unexpected behavior.
  {
    path: '**',
    redirectTo: 'error/404',
  },
];

export const routerOptions: ExtraOptions = {
  paramsInheritanceStrategy: 'always',
};

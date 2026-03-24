import { ExtraOptions, Routes } from '@angular/router';

import { isFeatureEnabled } from '@shared/config';
import {
  AuthorizeGuard,
  LoggedInGuard,
  NonAuthGuard,
  PendingRequestGuard,
  TermsAndConditionsGuard,
} from '@shared/guards';

import { AccessibilityComponent } from './accessibility/accessibility.component';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { DashboardPageComponent } from './dashboard/dashboard-page/dashboard-page.component';
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
  // unauthorized routes
  {
    path: '',
    data: { breadcrumb: 'Home' },
    children: [
      {
        path: 'about',
        data: { pageTitle: 'About' },
        component: VersionComponent,
      },
      {
        path: 'privacy-notice',
        data: { pageTitle: 'Privacy notice' },
        component: PrivacyNoticeComponent,
      },
      {
        path: 'accessibility',
        data: { pageTitle: 'Accessibility Statement' },
        component: AccessibilityComponent,
      },
      {
        path: 'contact-us',
        data: { pageTitle: 'Contact us' },
        component: ContactUsComponent,
      },
      {
        path: 'legislation',
        data: { pageTitle: 'CCA legislation' },
        component: LegislationComponent,
      },
      {
        path: 'feedback',
        data: { pageTitle: 'Feedback' },
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
    path: 'registration',
    children: [
      {
        path: 'invitation',
        loadChildren: () =>
          import('./invitation/operator-user-invitation/routes').then((r) => r.OPERATOR_INVITATION_USER_ROUTES),
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
  // authorized routes
  {
    path: '',
    canActivate: [LoggedInGuard],
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
        data: { breadcrumb: 'Manage Sectors' },
        loadChildren: () => import('./sectors/sectors.routes').then((r) => r.SECTORS_ROUTES),
      },
      {
        path: 'target-unit-accounts',
        data: { breadcrumb: 'Target Unit Accounts' },
        loadChildren: () =>
          import('./target-unit-accounts/target-unit-accounts.routes').then((r) => r.TARGET_UNIT_ACCOUNT_ROUTES),
      },
      {
        path: 'tasks',
        canActivate: [AuthorizeGuard],
        loadChildren: () => import('./requests/tasks/tasks.routes').then((r) => r.TASKS_ROUTES),
      },
      {
        path: 'mi-reports',
        loadChildren: () => import('./mi-reports/mi-reports.routes').then((r) => r.MI_REPORTS_ROUTES),
      },
      {
        path: 'subsistence-fees',
        canMatch: [() => !isFeatureEnabled('subsistenceFeesHideMenu')()],
        canActivate: [AuthorizeGuard],
        loadChildren: () => import('./subsistence-fees/subsistence-fees.routes').then((r) => r.SUBSISTENCE_FEES_ROUTES),
      },
      {
        path: 'buyout-surplus',
        canActivate: [AuthorizeGuard],
        loadChildren: () => import('./buy-out-surplus/buy-out-surplus.routes').then((r) => r.BUY_OUT_SURPLUS_ROUTES),
      },
      {
        path: 'templates',
        canActivate: [AuthorizeGuard],
        loadChildren: () => import('./templates/templates.routes').then((r) => r.TEMPLATES_ROUTES),
      },
      {
        path: 'sector-templates',
        canActivate: [AuthorizeGuard],
        loadComponent: () =>
          import('./templates/sector-templates-container.component').then((c) => c.SectorTemplatesContainerComponent),
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

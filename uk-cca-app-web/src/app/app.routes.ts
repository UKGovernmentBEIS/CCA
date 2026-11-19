import { ExtraOptions, Routes } from '@angular/router';

import { isFeatureEnabled } from '@shared/config';
import {
  AuthorizeGuard,
  LoggedInGuard,
  NonAuthGuard,
  PendingRequestGuard,
  TermsAndConditionsGuard,
} from '@shared/guards';

import { dashboardRequestTypeGuard } from './dashboard/dashboard-request-type.guard';
import { LandingPageGuard } from './landing-page/landing-page.guard';

export const APP_ROUTES: Routes = [
  {
    title: 'Manage your Climate Change Agreements (CCA)',
    path: 'landing',
    loadComponent: () => import('./landing-page/landing-page.component').then((c) => c.LandingPageComponent),
    canActivate: [LandingPageGuard],
    data: { titleFullServiceName: true, breadcrumb: 'Home' },
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'landing',
  },
  // unauthorized routes
  {
    path: '',
    children: [
      {
        title: 'About',
        path: 'about',
        loadComponent: () => import('./version/version.component').then((c) => c.VersionComponent),
      },
      {
        title: 'Privacy notice',
        path: 'privacy-notice',
        loadComponent: () => import('./privacy-notice/privacy-notice.component').then((c) => c.PrivacyNoticeComponent),
      },
      {
        title: 'Accessibility Statement',
        path: 'accessibility',
        loadComponent: () => import('./accessibility/accessibility.component').then((c) => c.AccessibilityComponent),
      },
      {
        title: 'Contact us',
        path: 'contact-us',
        loadComponent: () => import('./contact-us/contact-us.component').then((c) => c.ContactUsComponent),
      },
      {
        title: 'CCA legislation',
        path: 'legislation',
        loadComponent: () => import('./legislation/legislation.component').then((c) => c.LegislationComponent),
      },
      {
        title: 'Feedback',
        path: 'feedback',
        loadComponent: () => import('./feedback/feedback.component').then((c) => c.FeedbackComponent),
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
    title: 'Session Timeout',
    path: 'timed-out',
    loadComponent: () => import('./timeout/timed-out/timed-out.component').then((c) => c.TimedOutComponent),
    canActivate: [NonAuthGuard],
  },
  // authorized routes
  {
    path: '',
    canActivate: [LoggedInGuard],
    data: { breadcrumb: 'Dashboard' },
    children: [
      {
        title: 'Climate Change Agreements dashboard',
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard-page/dashboard-page.component').then((c) => c.DashboardPageComponent),
        canActivate: [AuthorizeGuard, dashboardRequestTypeGuard],
        runGuardsAndResolvers: 'paramsOrQueryParamsChange',
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
        canActivate: [AuthorizeGuard],
        canMatch: [() => !isFeatureEnabled('subsistenceFeesHideMenu')()],
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
        title: 'Templates',
        path: 'sector-templates',
        loadComponent: () =>
          import('./templates/sector-templates-container.component').then((c) => c.SectorTemplatesContainerComponent),
        canActivate: [AuthorizeGuard],
      },
      {
        title: 'Accept terms and conditions',
        path: 'terms',
        loadComponent: () =>
          import('./terms-and-conditions/terms-and-conditions.component').then((c) => c.TermsAndConditionsComponent),
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

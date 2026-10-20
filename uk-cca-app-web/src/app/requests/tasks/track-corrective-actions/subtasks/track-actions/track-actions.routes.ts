import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { trackCorrectiveActionsQuery } from '../../track-corrective-actions.selectors';
import { trackActionsRedirectGuard } from './track-actions.guard';

export const TRACK_ACTIONS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [trackActionsRedirectGuard],
        children: [],
      },
      {
        path: 'is-carried-out',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./is-carried-out/track-corrective-actions-is-carried-out.component').then(
            (c) => c.TrackCorrectiveActionsIsCarriedOutComponent,
          ),
      },
      {
        path: 'details',
        data: { breadcrumb: false, backlink: '../is-carried-out' },
        loadComponent: () =>
          import('./details/track-corrective-actions-details.component').then(
            (c) => c.TrackCorrectiveActionsDetailsComponent,
          ),
      },
      {
        path: 'check-your-answers',
        resolve: {
          isActionCarriedOut: (route: ActivatedRouteSnapshot) =>
            inject(RequestTaskStore).select(trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions)()
              ?.correctiveActionResponses[route.params.actionId]?.isActionCarriedOut,
        },
        data: {
          breadcrumb: false,
          backlink: ({ isActionCarriedOut }) => (isActionCarriedOut ? '../details' : '../is-carried-out'),
        },
        loadComponent: () =>
          import('./check-your-answers/track-corrective-actions-check-your-answers.component').then(
            (c) => c.TrackCorrectiveActionsCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./summary/track-corrective-actions-summary.component').then(
            (c) => c.TrackCorrectiveActionsSummaryComponent,
          ),
      },
    ],
  },
];

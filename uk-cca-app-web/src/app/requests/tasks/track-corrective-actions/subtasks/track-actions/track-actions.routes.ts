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
        title: 'Has the corrective action been carried out?',
        path: 'is-carried-out',
        loadComponent: () =>
          import('./is-carried-out/track-corrective-actions-is-carried-out.component').then(
            (c) => c.TrackCorrectiveActionsIsCarriedOutComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
      {
        title: 'Corrective actions carried out details',
        path: 'details',
        loadComponent: () =>
          import('./details/track-corrective-actions-details.component').then(
            (c) => c.TrackCorrectiveActionsDetailsComponent,
          ),
        data: { breadcrumb: false, backlink: '../is-carried-out' },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/track-corrective-actions-check-your-answers.component').then(
            (c) => c.TrackCorrectiveActionsCheckYourAnswersComponent,
          ),
        data: {
          breadcrumb: false,
          backlink: ({ isActionCarriedOut }: { isActionCarriedOut: boolean }) =>
            isActionCarriedOut ? '../details' : '../is-carried-out',
        },
        resolve: {
          isActionCarriedOut: (route: ActivatedRouteSnapshot) =>
            inject(RequestTaskStore).select(trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions)()
              ?.correctiveActionResponses[route.params.actionId]?.isActionCarriedOut,
        },
      },
      {
        title: 'Check your answers',
        path: 'summary',
        loadComponent: () =>
          import('./summary/track-corrective-actions-summary.component').then(
            (c) => c.TrackCorrectiveActionsSummaryComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
    ],
  },
];

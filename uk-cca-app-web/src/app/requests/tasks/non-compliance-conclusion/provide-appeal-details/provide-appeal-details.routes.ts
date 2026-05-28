import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, Routes } from '@angular/router';

import { nonComplianceConclusionEditableGuard } from '../non-compliance-conclusion.guard';
import { ProvideAppealDetailsStore } from './+state';

export const PROVIDE_APPEAL_DETAILS_ROUTES: Routes = [
  {
    path: '',
    providers: [ProvideAppealDetailsStore],
    canDeactivate: [
      () => {
        inject(ProvideAppealDetailsStore).reset();
        return true;
      },
    ],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'provide-details',
      },
      {
        path: 'provide-details',
        title: 'Provide appeal details',
        canActivate: [nonComplianceConclusionEditableGuard],
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideAppealDetailsComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        canActivate: [
          nonComplianceConclusionEditableGuard,
          (route: ActivatedRouteSnapshot) =>
            inject(ProvideAppealDetailsStore).state.appealDetails
              ? true
              : createUrlTreeFromSnapshot(route, ['../provide-details']),
        ],
        data: { backlink: '../provide-details', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
      },
      {
        path: 'confirmation',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
      },
    ],
  },
];

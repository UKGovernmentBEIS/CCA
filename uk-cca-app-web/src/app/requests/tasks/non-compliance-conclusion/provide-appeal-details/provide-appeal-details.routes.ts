import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, Routes } from '@angular/router';

import { nonComplianceConclusionEditableGuard } from '../non-compliance-conclusion.guard';
import { ProvideAppealDetailsStore } from './+state';

export const PROVIDE_APPEAL_DETAILS_ROUTES: Routes = [
  {
    path: '',
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
        title: 'Provide appeal details',
        path: 'provide-details',
        loadComponent: () =>
          import('./provide-details/provide-details.component').then((c) => c.ProvideAppealDetailsComponent),
        canActivate: [nonComplianceConclusionEditableGuard],
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
        canActivate: [
          nonComplianceConclusionEditableGuard,
          (route: ActivatedRouteSnapshot) =>
            inject(ProvideAppealDetailsStore).state.appealDetails
              ? true
              : createUrlTreeFromSnapshot(route, ['../provide-details']),
        ],
        data: { backlink: '../provide-details', breadcrumb: false },
      },
      {
        title: 'Appeal registration details submitted',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
        data: { backlink: false, breadcrumb: false },
      },
    ],
    providers: [ProvideAppealDetailsStore],
  },
];

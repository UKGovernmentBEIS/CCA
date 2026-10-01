import { userIsRegulatorGuard } from '@shared/guards';

import { editSurplusGuard } from './edit-surplus/edit-surplus-guard';
import { surplusHistoryGuard } from './surplus-history/surplus-history.guard';

export const BUYOUT_AND_SURPLUS_ROUTES = [
  {
    path: 'buyout-surplus',
    children: [
      {
        path: 'edit-on-hold',
        title: 'Do you want to put this target unit on hold for the next buy-out and surplus batch?',
        canActivate: [userIsRegulatorGuard],
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () => import('./edit-on-hold/edit-on-hold.component').then((c) => c.EditOnHoldComponent),
      },
      {
        path: 'edit-on-hold/confirmation',
        title: 'On hold status updated',
        canActivate: [userIsRegulatorGuard],
        loadComponent: () =>
          import('./edit-on-hold/confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
      },
      {
        path: ':targetPeriodType/surplus-history',
        title: 'Surplus gained change history',
        canActivate: [surplusHistoryGuard],
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./surplus-history/surplus-history.component').then((c) => c.SurplusHistoryComponent),
      },
      {
        path: ':targetPeriodType/edit-surplus',
        title: 'Surplus gained',
        canActivate: [userIsRegulatorGuard, editSurplusGuard],
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () => import('./edit-surplus/edit-surplus.component').then((c) => c.EditSurplusComponent),
      },
      {
        path: ':targetPeriodType/edit-surplus/confirmation',
        title: 'Surplus gained updated',
        canActivate: [userIsRegulatorGuard],
        data: { breadcrumb: false, backlink: '../../../../' },
        loadComponent: () =>
          import('./edit-surplus/confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
      },
    ],
  },
];

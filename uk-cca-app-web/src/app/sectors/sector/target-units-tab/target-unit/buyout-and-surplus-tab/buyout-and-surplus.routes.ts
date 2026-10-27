import { userIsRegulatorGuard } from '@shared/guards';

import { editSurplusGuard } from './edit-surplus/edit-surplus.guard';
import { surplusHistoryGuard } from './surplus-history/surplus-history.guard';

export const BUYOUT_AND_SURPLUS_ROUTES = [
  {
    path: 'buyout-surplus',
    children: [
      {
        title: 'Do you want to put this target unit on hold for the next buy-out and surplus batch?',
        path: 'edit-on-hold',
        loadComponent: () => import('./edit-on-hold/edit-on-hold.component').then((c) => c.EditOnHoldComponent),
        canActivate: [userIsRegulatorGuard],
        data: { breadcrumb: false, backlink: { link: '../../', fragment: 'buyout-surplus' } },
      },
      {
        title: 'On hold status updated',
        path: 'edit-on-hold/confirmation',
        loadComponent: () =>
          import('./edit-on-hold/confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
        canActivate: [userIsRegulatorGuard],
      },
      {
        title: 'Surplus gained change history',
        path: 'surplus-history',
        loadComponent: () =>
          import('./surplus-history/surplus-history.component').then((c) => c.SurplusHistoryComponent),
        canActivate: [surplusHistoryGuard],
        data: { breadcrumb: false, backlink: { link: '../../', fragment: 'buyout-surplus' } },
      },
      {
        title: 'Surplus gained',
        path: 'edit-surplus',
        loadComponent: () => import('./edit-surplus/edit-surplus.component').then((c) => c.EditSurplusComponent),
        canActivate: [userIsRegulatorGuard, editSurplusGuard],
        data: { breadcrumb: false, backlink: { link: '../../', fragment: 'buyout-surplus' } },
      },
      {
        title: 'Surplus gained updated',
        path: 'edit-surplus/confirmation',
        loadComponent: () =>
          import('./edit-surplus/confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
        canActivate: [userIsRegulatorGuard],
        data: { breadcrumb: false, backlink: { link: '../../../', fragment: 'buyout-surplus' } },
      },
    ],
  },
];

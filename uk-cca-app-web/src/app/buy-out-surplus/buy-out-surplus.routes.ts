import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { BuyoutSurplusStore } from './buy-out-surplus.store';
import { paymentStatusRedirectGuard } from './payment-status.guard';

export const BUY_OUT_SURPLUS_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    providers: [BuyoutSurplusStore],
    canActivate: [paymentStatusRedirectGuard],
    canDeactivate: [
      () => {
        inject(BuyoutSurplusStore).reset();
        return true;
      },
    ],
    loadComponent: () => import('./buy-out-surplus.component').then((c) => c.BuyOutSurplusComponent),
  },
  {
    path: 'new-batch',
    data: {
      breadcrumb: {
        text: 'Buy-out and surplus',
        fragment: 'transactions',
        link: '/buyout-surplus',
      },
    },
    loadComponent: () => import('./new-batch/new-batch.component').then((c) => c.NewBatchComponent),
  },
  {
    path: 'workflow-history',
    data: {
      breadcrumb: {
        text: 'Buy-out and surplus',
        fragment: 'workflow-history',
        link: '/buyout-surplus',
      },
    },
    loadChildren: () =>
      import('./workflow-history/workflow-history.routes').then((r) => r.BUY_OUT_SURPLUS_WORKFLOW_HISTORY_ROUTES),
  },
  {
    path: 'transactions',
    loadChildren: () =>
      import('./transactions-list-tab/transaction-details-container/transaction-details.routes').then(
        (r) => r.TRANSACTION_DETAILS_ROUTES,
      ),
  },
  {
    path: 'confirmation',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
  {
    path: 'request-error',
    loadComponent: () => import('./request-error/request-error.component').then((c) => c.RequestErrorComponent),
  },
];

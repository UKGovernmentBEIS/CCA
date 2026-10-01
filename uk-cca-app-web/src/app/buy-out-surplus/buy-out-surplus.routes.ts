import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { map } from 'rxjs';

import { userIsRegulatorGuard } from '@shared/guards';

import { BuyOutAndSurplusCostInfoService } from 'cca-api';

import { BuyoutSurplusStore } from './buy-out-surplus.store';
import { AvailableTargetPeriodsResolver } from './new-batch/new-batch.resolver';
import { paymentStatusRedirectGuard } from './payment-status.guard';

export const BUY_OUT_SURPLUS_ROUTES: Routes = [
  {
    path: '',
    title: 'Buy-out and surplus',
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
    title: 'New buy-out and surplus batch',
    data: {
      breadcrumb: {
        text: 'Buy-out and surplus',
        fragment: 'transactions',
        link: '/buyout-surplus',
      },
    },
    resolve: { availableTargetPeriods: AvailableTargetPeriodsResolver },
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
    path: 'cost/:targetPeriodType',
    canActivate: [userIsRegulatorGuard],
    resolve: {
      targetPeriodDetails: (route: ActivatedRouteSnapshot) =>
        inject(BuyOutAndSurplusCostInfoService)
          .getBuyOutCosts('CCA_3')
          .pipe(map((costs) => costs.find(({ businessId }) => businessId === route.paramMap.get('targetPeriodType')))),
    },
    children: [
      {
        path: '',
        title: 'Buy-out cost per tCO2e',
        data: { breadcrumb: false, backlink: '../..' },
        loadComponent: () => import('./cost-tab/change-cost/change-cost.component').then((c) => c.ChangeCostComponent),
      },
      {
        path: 'confirmation',
        title: 'Buy-out cost per tCO2e updated',
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./cost-tab/change-cost/confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
      },
    ],
  },
  {
    path: 'confirmation',
    title: 'Batch in progress',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
  {
    path: 'request-error',
    title: 'New buy-out and surplus batch',
    loadComponent: () => import('./request-error/request-error.component').then((c) => c.RequestErrorComponent),
  },
];

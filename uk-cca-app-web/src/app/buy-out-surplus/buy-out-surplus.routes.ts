import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { map } from 'rxjs';

import { RouteBacklink } from '@netz/common/navigation';
import { userIsRegulatorGuard } from '@shared/guards';
import { SchemeVersionEnum } from '@shared/types';

import { BuyOutAndSurplusCostInfoService } from 'cca-api';

import { BuyoutSurplusStore } from './buy-out-surplus.store';
import { AvailableTargetPeriodsResolver } from './new-batch/new-batch.resolver';
import { paymentStatusRedirectGuard } from './payment-status.guard';

export const BUY_OUT_SURPLUS_ROUTES: Routes = [
  {
    title: 'Buy-out and surplus',
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./buy-out-surplus.component').then((c) => c.BuyOutSurplusComponent),
    canActivate: [paymentStatusRedirectGuard],
    canDeactivate: [
      () => {
        inject(BuyoutSurplusStore).reset();
        return true;
      },
    ],
    providers: [BuyoutSurplusStore],
  },
  {
    title: 'New buy-out and surplus batch',
    path: 'new-batch',
    loadComponent: () => import('./new-batch/new-batch.component').then((c) => c.NewBatchComponent),
    data: {
      breadcrumb: {
        text: 'Buy-out and surplus',
        fragment: 'transactions',
        link: '/buyout-surplus',
      },
    },
    resolve: { availableTargetPeriods: AvailableTargetPeriodsResolver },
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
    path: 'cca3-transactions',
    loadChildren: () =>
      import('./transactions-list-tab/cca3-transactions/cca3-transactions.routes').then(
        (r) => r.CCA3_TRANSACTIONS_ROUTES,
      ),
  },
  {
    path: 'cost/:targetPeriodType',
    canActivate: [userIsRegulatorGuard],
    resolve: {
      targetPeriodDetails: (route: ActivatedRouteSnapshot) =>
        inject(BuyOutAndSurplusCostInfoService)
          .getBuyOutCosts(SchemeVersionEnum.CCA_3)
          .pipe(map((costs) => costs.find(({ businessId }) => businessId === route.paramMap.get('targetPeriodType')))),
    },
    children: [
      {
        title: 'Buy-out cost per tCO2e',
        path: '',
        loadComponent: () => import('./cost-tab/change-cost/change-cost.component').then((c) => c.ChangeCostComponent),
        data: {
          breadcrumb: false,
          // Entered from the cost tab or from the new batch page's missing cost error
          backlink: ((_data, route) =>
            route.queryParams.from === 'new-batch'
              ? { link: '../../new-batch', queryParams: {} }
              : { link: '../..', fragment: 'cost' }) satisfies RouteBacklink,
        },
      },
      {
        title: 'Buy-out cost per tCO2e updated',
        path: 'confirmation',
        loadComponent: () =>
          import('./cost-tab/change-cost/confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
        data: { breadcrumb: false, backlink: false },
      },
    ],
  },
  {
    title: 'Batch in progress',
    path: 'confirmation',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
  {
    title: 'New buy-out and surplus batch',
    path: 'request-error',
    loadComponent: () => import('./request-error/request-error.component').then((c) => c.RequestErrorComponent),
  },
];

import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { BuyOutAndSurplusAccountTransactionViewService } from 'cca-api';

export const TRANSACTION_DETAILS_ROUTES: Routes = [
  {
    path: ':transactionId',
    data: {
      breadcrumb: {
        text: 'Buy-out and surplus',
        fragment: 'transactions',
        link: '/buyout-surplus',
      },
    },
    resolve: {
      transactionDetails: (route: ActivatedRouteSnapshot) =>
        inject(BuyOutAndSurplusAccountTransactionViewService).getAccountBuyOutSurplusTransaction(
          +route.paramMap.get('transactionId'),
        ),
    },
    children: [
      {
        title: 'Transaction details',
        path: '',
        loadComponent: () =>
          import('./transaction-details-container.component').then((c) => c.TransactionDetailsContainerComponent),
      },
      {
        path: 'change-status',
        children: [
          {
            title: 'Status',
            path: '',
            loadComponent: () =>
              import('./transaction-details-tab/change-status/change-status.component').then(
                (c) => c.ChangeStatusComponent,
              ),
            data: { breadcrumb: false, backlink: '../' },
          },
          {
            title: 'Payment status updated',
            path: 'confirmation',
            loadComponent: () =>
              import('./transaction-details-tab/change-status/confirmation/confirmation.component').then(
                (c) => c.ConfirmationComponent,
              ),
            data: { breadcrumb: false },
          },
        ],
      },
      {
        path: 'change-amount',
        children: [
          {
            title: 'Amount',
            path: '',
            loadComponent: () =>
              import('./transaction-details-tab/change-amount/change-amount.component').then(
                (c) => c.ChangeAmountComponent,
              ),
            data: { breadcrumb: false, backlink: '../' },
          },
          {
            title: 'Current amount updated',
            path: 'confirmation',
            loadComponent: () =>
              import('./transaction-details-tab/change-amount/confirmation/confirmation-component').then(
                (c) => c.ConfirmationComponent,
              ),
            data: { breadcrumb: false },
          },
        ],
      },
      {
        title: 'Your download has started',
        path: 'file-download/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
      },
      {
        title: 'Your download has started',
        path: 'file-evidences-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.EvidenceFileDownloadComponent),
        data: { evidenceResource: 'transactionDetail' },
      },
    ],
    runGuardsAndResolvers: 'always',
  },
];

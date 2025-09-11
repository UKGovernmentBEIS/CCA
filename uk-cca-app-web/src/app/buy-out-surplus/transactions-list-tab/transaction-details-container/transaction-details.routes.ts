import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { BuyOutAndSurplusTransactionInfoViewService } from 'cca-api';

export const TRANSACTION_DETAILS_ROUTES: Routes = [
  {
    path: ':transactionId',
    resolve: {
      transactionDetails: (route: ActivatedRouteSnapshot) =>
        inject(BuyOutAndSurplusTransactionInfoViewService).getBuyOutSurplusTransactionDetails(
          +route.paramMap.get('transactionId'),
        ),
    },
    runGuardsAndResolvers: 'always',
    data: {
      breadcrumb: {
        text: 'Buy-out and surplus',
        fragment: 'transactions',
        link: '/buyout-surplus',
      },
    },
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./transaction-details-container.component').then((c) => c.TransactionDetailsContainerComponent),
      },
      {
        path: 'change-status',
        children: [
          {
            path: '',
            data: { breadcrumb: false, backlink: '../' },
            loadComponent: () =>
              import('./transcation-details-tab/change-status/change-status.component').then(
                (c) => c.ChangeStatusComponent,
              ),
          },
          {
            path: 'confirmation',
            data: { breadcrumb: false },
            loadComponent: () =>
              import('./transcation-details-tab/change-status/confirmation/confirmation.component').then(
                (c) => c.ConfirmationComponent,
              ),
          },
        ],
      },
      {
        path: 'change-amount',
        children: [
          {
            path: '',
            data: { breadcrumb: false, backlink: '../' },
            loadComponent: () =>
              import('./transcation-details-tab/change-amount/change-amount.component').then(
                (c) => c.ChangeAmountComponent,
              ),
          },
          {
            path: 'confirmation',
            data: { breadcrumb: false },
            loadComponent: () =>
              import('./transcation-details-tab/change-amount/confirmation/confirmation-component').then(
                (c) => c.ConfirmationComponent,
              ),
          },
        ],
      },
      {
        path: 'file-download/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
      {
        path: 'file-evidences-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.EvidenceFileDownloadComponent),
      },
    ],
  },
];

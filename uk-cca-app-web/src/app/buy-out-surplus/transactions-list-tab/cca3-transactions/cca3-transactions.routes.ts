import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import {
  canActivateCca3ChangeAmount,
  canActivateCca3ChangeAmountReview,
} from './cca3-transaction-details/cca3-change-amount/cca3-change-amount.guard';
import { cca3ChangeAmountFacilityResolver } from './cca3-transaction-details/cca3-change-amount/cca3-change-amount.resolver';
import { Cca3ChangeAmountState } from './cca3-transaction-details/cca3-change-amount/cca3-change-amount.state';
import { cca3ChangeAmountFormProvider } from './cca3-transaction-details/cca3-change-amount/cca3-change-amount-form.provider';
import { canActivateCca3ChangeHistory } from './cca3-transaction-details/cca3-change-history/cca3-change-history.guard';
import { canActivateCca3ChangeStatus } from './cca3-transaction-details/cca3-change-status/cca3-change-status.guard';
import { canActivateCca3MarkAllFacilities } from './cca3-transaction-details/cca3-mark-all-facilities/cca3-mark-all-facilities.guard';
import {
  CCA3_MARK_ALL_CONTENT,
  Cca3MarkAllStatus,
} from './cca3-transaction-details/cca3-mark-all-facilities/cca3-mark-all-facilities.types';
import { canDeactivateCca3TransactionDetails } from './cca3-transaction-details/cca3-transaction-details.guard';
import { Cca3TransactionDetailsStore } from './cca3-transaction-details/cca3-transaction-details.store';

const markAllFacilitiesRoutes = (markAllStatus: Cca3MarkAllStatus): Routes => [
  {
    title: CCA3_MARK_ALL_CONTENT[markAllStatus].heading,
    path: '',
    loadComponent: () =>
      import('./cca3-transaction-details/cca3-mark-all-facilities/cca3-mark-all-facilities.component').then(
        (c) => c.Cca3MarkAllFacilitiesComponent,
      ),
    canActivate: [canActivateCca3MarkAllFacilities],
    data: { breadcrumb: false, backlink: '../../' },
  },
  {
    title: CCA3_MARK_ALL_CONTENT[markAllStatus].confirmation,
    path: 'confirmation',
    loadComponent: () =>
      import('./cca3-transaction-details/cca3-mark-all-facilities/confirmation/cca3-mark-all-facilities-confirmation.component').then(
        (c) => c.Cca3MarkAllFacilitiesConfirmationComponent,
      ),
    data: { breadcrumb: false, backlink: false },
  },
];

export const CCA3_TRANSACTIONS_ROUTES: Routes = [
  {
    path: ':transactionId',
    canDeactivate: [canDeactivateCca3TransactionDetails],
    data: {
      breadcrumb: {
        text: 'Buy-out and surplus',
        fragment: 'transactions',
        link: '/buyout-surplus',
      },
    },
    resolve: {
      transactionDetails: (route: ActivatedRouteSnapshot) =>
        inject(BuyOutAndSurplusFacilityTransactionViewService).getFacilityBuyOutSurplusTransaction(
          +route.paramMap.get('transactionId'),
        ),
    },
    children: [
      {
        title: 'Transaction details',
        path: '',
        loadComponent: () =>
          import('./cca3-transaction-details/cca3-transaction-details.component').then(
            (c) => c.Cca3TransactionDetailsComponent,
          ),
      },
      {
        path: 'received-amount',
        loadChildren: () =>
          import('./cca3-transaction-details/cca3-received-amount/cca3-received-amount.routes').then(
            (r) => r.CCA3_RECEIVED_AMOUNT_ROUTES,
          ),
      },
      {
        path: 'facilities',
        children: [
          {
            path: ':facilityBusinessId',
            children: [
              {
                title: 'Change history',
                path: 'change-history',
                loadComponent: () =>
                  import('./cca3-transaction-details/cca3-change-history/cca3-change-history.component').then(
                    (c) => c.Cca3ChangeHistoryComponent,
                  ),
                canActivate: [canActivateCca3ChangeHistory],
                data: { breadcrumb: false, backlink: '../../../' },
                resolve: { facilityDetails: cca3ChangeAmountFacilityResolver },
              },
              {
                title: 'Your download has started',
                path: 'file-evidences-download/:transactionDetailId/:fileType/:uuid',
                loadComponent: () => import('@shared/components').then((c) => c.EvidenceFileDownloadComponent),
                canActivate: [canActivateCca3ChangeHistory],
              },
              {
                path: 'change-amount',
                loadComponent: () =>
                  import('./cca3-transaction-details/cca3-change-amount/cca3-change-amount-container.component').then(
                    (c) => c.Cca3ChangeAmountContainerComponent,
                  ),
                canActivate: [canActivateCca3ChangeAmount],
                resolve: { facilityDetails: cca3ChangeAmountFacilityResolver },
                children: [
                  {
                    title: 'Current amount',
                    path: '',
                    loadComponent: () =>
                      import('./cca3-transaction-details/cca3-change-amount/cca3-change-amount.component').then(
                        (c) => c.Cca3ChangeAmountComponent,
                      ),
                    data: { breadcrumb: false, backlink: '../../../' },
                  },
                  {
                    title: 'Check your answers',
                    path: 'check-your-answers',
                    loadComponent: () =>
                      import('./cca3-transaction-details/cca3-change-amount/check-your-answers/cca3-change-amount-review.component').then(
                        (c) => c.Cca3ChangeAmountReviewComponent,
                      ),
                    canActivate: [canActivateCca3ChangeAmountReview],
                    data: { breadcrumb: false, backlink: '../' },
                  },
                  {
                    title: 'Current amount updated',
                    path: 'confirmation',
                    loadComponent: () =>
                      import('./cca3-transaction-details/cca3-change-amount/confirmation/cca3-change-amount-confirmation.component').then(
                        (c) => c.Cca3ChangeAmountConfirmationComponent,
                      ),
                    data: { breadcrumb: false, backlink: false },
                  },
                ],
                providers: [cca3ChangeAmountFormProvider, Cca3ChangeAmountState],
              },
            ],
          },
        ],
      },
      {
        path: 'change-status',
        children: [
          {
            title: 'Status for selected facilities',
            path: '',
            loadComponent: () =>
              import('./cca3-transaction-details/cca3-change-status/cca3-change-status.component').then(
                (c) => c.Cca3ChangeStatusComponent,
              ),
            canActivate: [canActivateCca3ChangeStatus],
            data: { breadcrumb: false, backlink: '../' },
          },
          {
            title: 'Payment status updated',
            path: 'confirmation',
            loadComponent: () =>
              import('./cca3-transaction-details/cca3-change-status/confirmation/cca3-change-status-confirmation.component').then(
                (c) => c.Cca3ChangeStatusConfirmationComponent,
              ),
            data: { breadcrumb: false, backlink: false },
          },
        ],
      },
      {
        path: 'mark-facilities',
        children: [
          {
            path: 'all-paid',
            data: { markAllStatus: 'PAID' },
            children: markAllFacilitiesRoutes('PAID'),
          },
          {
            path: 'all-refunded',
            data: { markAllStatus: 'REFUNDED' },
            children: markAllFacilitiesRoutes('REFUNDED'),
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
      },
    ],
    runGuardsAndResolvers: 'always',
    providers: [Cca3TransactionDetailsStore],
  },
];

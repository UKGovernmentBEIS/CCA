import { Routes } from '@angular/router';

import { canActivateCca3ReceivedAmount, canActivateCca3ReceivedAmountReview } from './cca3-received-amount.guard';
import { cca3ReceivedAmountHistoryResolver } from './cca3-received-amount.resolver';
import { Cca3ReceivedAmountState } from './cca3-received-amount.state';
import { cca3ReceivedAmountHistoryEntryResolver } from './history-details/cca3-received-amount-history.resolver';

export const CCA3_RECEIVED_AMOUNT_ROUTES: Routes = [
  {
    path: '',
    providers: [Cca3ReceivedAmountState],
    canActivate: [canActivateCca3ReceivedAmount],
    resolve: {
      receivedAmountHistory: cca3ReceivedAmountHistoryResolver,
    },
    loadComponent: () =>
      import('./cca3-received-amount-container.component').then((c) => c.Cca3ReceivedAmountContainerComponent),
    children: [
      {
        path: '',
        title: 'Received amount',
        data: { breadcrumb: false, backlink: '..' },
        loadComponent: () => import('./cca3-received-amount.component').then((c) => c.Cca3ReceivedAmountComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        canActivate: [canActivateCca3ReceivedAmountReview],
        data: { breadcrumb: false, backlink: '..' },
        loadComponent: () =>
          import('./check-your-answers/cca3-received-amount-review.component').then(
            (c) => c.Cca3ReceivedAmountReviewComponent,
          ),
      },
      {
        path: 'history-details/:detailsId',
        title: 'Received amount history',
        data: { breadcrumb: false, backlink: '../..' },
        resolve: {
          historyEntry: cca3ReceivedAmountHistoryEntryResolver,
        },
        loadComponent: () =>
          import('./history-details/cca3-received-amount-history.component').then(
            (c) => c.Cca3ReceivedAmountHistoryComponent,
          ),
      },
      {
        path: 'confirmation',
        title: 'Received amount updated',
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./confirmation/cca3-received-amount-confirmation.component').then(
            (c) => c.Cca3ReceivedAmountConfirmationComponent,
          ),
      },
    ],
  },
];

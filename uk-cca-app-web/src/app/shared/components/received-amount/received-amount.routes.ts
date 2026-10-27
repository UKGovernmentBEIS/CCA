import { Routes } from '@angular/router';

import { ReceivedAmountGuard } from './received-amount.guard';

export const RECEIVED_AMOUNT_ROUTES: Routes = [
  {
    title: 'Received amount',
    path: '',
    loadComponent: () => import('./received-amount.component').then((c) => c.ReceivedAmountComponent),
    data: { backlink: '..', breadcrumb: false },
  },
  {
    title: 'Received amount history',
    path: 'history-details/:detailsId',
    loadComponent: () => import('./history-details/history-details.component').then((c) => c.HistoryDetailsComponent),
    canActivate: [ReceivedAmountGuard],
    data: { backlink: '../..', breadcrumb: false },
  },
  {
    title: 'Check your answers',
    path: 'check-your-answers',
    loadComponent: () =>
      import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
    canActivate: [ReceivedAmountGuard],
    data: { backlink: '..', breadcrumb: false },
  },
  {
    title: 'Received amount updated',
    path: 'confirmation',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
    canActivate: [ReceivedAmountGuard],
    data: { backlink: false, breadcrumb: false },
  },
  {
    title: 'Your download has started',
    path: 'evidence-file-download/:fileType/:uuid',
    loadComponent: () => import('@shared/components').then((c) => c.EvidenceFileDownloadComponent),
  },
];

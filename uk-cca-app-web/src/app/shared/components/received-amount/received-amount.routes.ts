import { Routes } from '@angular/router';

import { ReceivedAmountGuard } from './received-amount.guard';

export const SECTOR_MOAS_RECEIVED_AMOUNT_ROUTES: Routes = [
  {
    path: '',
    data: { backlink: '..', breadcrumb: false },
    loadComponent: () => import('./received-amount.component').then((c) => c.ReceivedAmountComponent),
  },
  {
    path: 'history-details/:detailsId',
    data: { backlink: '../..', breadcrumb: false },
    canActivate: [ReceivedAmountGuard],
    loadComponent: () => import('./history-details/history-details.component').then((c) => c.HistoryDetailsComponent),
  },
  {
    path: 'check-your-answers',
    data: { backlink: '..', breadcrumb: false },
    canActivate: [ReceivedAmountGuard],
    loadComponent: () =>
      import('./check-your-answers/check-your-answers.component').then((c) => c.CheckYourAnswersComponent),
  },
  {
    path: 'confirmation',
    data: { backlink: false, breadcrumb: false },
    canActivate: [ReceivedAmountGuard],
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
  {
    path: 'evidence-file-download/:fileType/:uuid',
    loadComponent: () => import('@shared/components').then((m) => m.EvidenceFileDownloadComponent),
  },
];

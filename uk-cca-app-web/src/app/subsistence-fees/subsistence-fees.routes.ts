import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { SubsistenceFeesStore } from './subsistence-fees.store';

export const SUBSISTENCE_FEES_ROUTES: Routes = [
  {
    title: 'Subsistence fees',
    path: '',
    loadComponent: () => import('./subsistence-fees.component').then((c) => c.SubsistenceFeesComponent),
    canDeactivate: [
      () => {
        inject(SubsistenceFeesStore).reset();
        return true;
      },
    ],
    providers: [SubsistenceFeesStore],
  },
  {
    title: 'New payment request',
    path: 'new-payment-request',
    loadComponent: () =>
      import('./new-subsistence-fees-payment-request/new-subsistence-fees-payment-request.component').then(
        (c) => c.NewSubsistenceFeesPaymentRequestComponent,
      ),
    data: {
      breadcrumb: {
        text: 'Subsistence fees',
        fragment: 'sent-subsistence-fees',
        link: '/subsistence-fees',
      },
    },
  },
  {
    path: 'workflow-history',
    data: {
      breadcrumb: {
        text: 'Subsistence fees',
        fragment: 'workflow-history',
        link: '/subsistence-fees',
      },
    },
    loadChildren: () =>
      import('./workflow-history/workflow-history.routes').then((r) => r.SUBSISTENCE_FEES_WORKFLOW_HISTORY_ROUTES),
  },
  {
    path: 'sent-subsistence-fees',
    loadChildren: () =>
      import('./sent-subsistence-fees/sent-subsistence-fees.routes').then((r) => r.SENT_SUBSISTENCE_FEES_ROUTES),
  },
  {
    title: 'Payment requests process in progress',
    path: 'confirmation',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConfirmationComponent),
  },
  {
    title: 'New payment request',
    path: 'request-error',
    loadComponent: () => import('./request-error/request-error.component').then((c) => c.RequestErrorComponent),
  },
];

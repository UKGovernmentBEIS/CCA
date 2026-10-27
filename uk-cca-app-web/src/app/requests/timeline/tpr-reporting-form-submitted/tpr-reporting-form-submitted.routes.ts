import { Routes } from '@angular/router';

export const TPR_REPORTING_FORM_SUBMITTED_ROUTES: Routes = [
  {
    path: 'energy-fuel-amount',
    loadComponent: () =>
      import('./subtasks/energy-fuel-amount-submitted/energy-fuel-amount-submitted.component').then(
        (c) => c.EnergyFuelAmountSubmittedComponent,
      ),
    data: { backlink: '../../', breadcrumb: false },
  },
  {
    path: 'throughput',
    loadComponent: () =>
      import('./subtasks/tpr-throughput-submitted/tpr-throughput-submitted.component').then(
        (c) => c.TprThroughputSubmittedComponent,
      ),
    data: { backlink: '../../', breadcrumb: false },
  },
  {
    path: 'submit',
    loadComponent: () =>
      import('./subtasks/submit-results/submit-results.component').then((c) => c.SubmitResultsComponent),
    data: { backlink: '../../', breadcrumb: false },
  },
  {
    title: 'View Products',
    path: 'products',
    loadComponent: () => import('@requests/common').then((c) => c.TprProductsTimelineComponent),
    data: { breadcrumb: false, backlink: '../submit' },
  },
];

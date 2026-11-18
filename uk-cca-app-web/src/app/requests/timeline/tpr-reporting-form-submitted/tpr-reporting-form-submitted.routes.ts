import { Routes } from '@angular/router';

export const TPR_REPORTING_FORM_SUBMITTED_ROUTES: Routes = [
  {
    path: 'energy-fuel-amount',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/energy-fuel-amount-submitted/energy-fuel-amount-submitted.component').then(
        (c) => c.EnergyFuelAmountSubmittedComponent,
      ),
  },
  {
    path: 'throughput',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/tpr-throughput-submitted/tpr-throughput-submitted.component').then(
        (c) => c.TprThroughputSubmittedComponent,
      ),
  },
  {
    path: 'submit',
    data: { backlink: '../../', breadcrumb: false },
    loadComponent: () =>
      import('./subtasks/submit-results/submit-results.component').then((c) => c.SubmitResultsComponent),
  },
  {
    path: 'products',
    title: 'View Products',
    data: { breadcrumb: false, backlink: '../submit' },
    loadComponent: () => import('@requests/common').then((c) => c.TprProductsTimelineComponent),
  },
];

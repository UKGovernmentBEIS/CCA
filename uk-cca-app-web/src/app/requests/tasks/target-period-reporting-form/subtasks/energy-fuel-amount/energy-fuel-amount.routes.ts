import { Routes } from '@angular/router';

import { energyFuelAmountRedirectGuard } from './energy-fuel-amount-redirect.guard';

export const ENERGY_FUEL_AMOUNT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [energyFuelAmountRedirectGuard],
        children: [],
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/energy-fuel-amount-details-summary.component').then(
            (c) => c.EnergyFuelAmountDetailsSummaryComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/energy-fuel-amount-details-check-your-answers.component').then(
            (c) => c.EnergyFuelAmountDetailsCheckYourAnswersComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Provide energy/fuel amount consumed',
        path: 'details',
        loadComponent: () =>
          import('./details/energy-fuel-amount-details.component').then((c) => c.EnergyFuelAmountDetailsComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];

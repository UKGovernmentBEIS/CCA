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
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./summary/energy-fuel-amount-details-summary.component').then(
            (c) => c.EnergyFuelAmountDetailsSummaryComponent,
          ),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/energy-fuel-amount-details-check-your-answers.component').then(
            (c) => c.EnergyFuelAmountDetailsCheckYourAnswersComponent,
          ),
      },
      {
        path: 'details',
        title: 'Provide energy/fuel amount consumed',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./details/energy-fuel-amount-details.component').then((c) => c.EnergyFuelAmountDetailsComponent),
      },
    ],
  },
];

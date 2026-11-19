import { Routes } from '@angular/router';

import { etsEnergyFuelAmountRedirectGuard } from './energy-fuel-amount-redirect.guard';

export const ETS_ENERGY_FUEL_AMOUNT_ROUTES: Routes = [
  {
    path: '',
    children: [
      { path: '', canActivate: [etsEnergyFuelAmountRedirectGuard], children: [] },
      {
        path: 'ultra-small-emitter',
        title: 'Is this facility registered as an ultra-small emitter (USE) in UK ETS?',
        loadComponent: () =>
          import('./ultra-small-emitter/ultra-small-emitter.component').then((c) => c.UltraSmallEmitterComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        path: 'details',
        title: 'Enter UK ETS energy/fuel amount consumed',
        loadComponent: () =>
          import('./details/energy-fuel-amount-details.component').then((c) => c.EnergyFuelAmountDetailsComponent),
        data: { backlink: '../ultra-small-emitter', breadcrumb: false },
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        loadComponent: () =>
          import('./check-your-answers/energy-fuel-amount-details-check-your-answers.component').then(
            (c) => c.EnergyFuelAmountDetailsCheckYourAnswersComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        path: 'summary',
        title: 'UK ETS energy/fuel details',
        loadComponent: () =>
          import('./summary/energy-fuel-amount-details-summary.component').then(
            (c) => c.EnergyFuelAmountDetailsSummaryComponent,
          ),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];

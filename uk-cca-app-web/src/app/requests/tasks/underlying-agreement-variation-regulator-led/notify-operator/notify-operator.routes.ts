import { Routes } from '@angular/router';

export const REGULATOR_LED_VARIATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    data: { backlink: '../..', breadcrumb: false },
    title: 'Notify operator of decision',
    loadComponent: () =>
      import('./notify-operator-regulator-led-variation.component').then(
        (c) => c.NotifyOperatorRegulatorLedVariationComponent,
      ),
  },
  {
    path: 'confirmation',
    data: { breadcrumb: false },
    title: 'Variation complete',
    loadComponent: () =>
      import('../confirmation/confirmation.component').then(
        (c) => c.NotifyOperatorRegulatorLedVariationConfirmationComponent,
      ),
  },
];

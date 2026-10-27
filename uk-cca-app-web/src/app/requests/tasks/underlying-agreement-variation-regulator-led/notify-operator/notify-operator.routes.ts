import { Routes } from '@angular/router';

export const REGULATOR_LED_VARIATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    title: 'Notify operator of decision',
    path: '',
    loadComponent: () =>
      import('./notify-operator-regulator-led-variation.component').then(
        (c) => c.NotifyOperatorRegulatorLedVariationComponent,
      ),
    data: { backlink: '../..', breadcrumb: false },
  },
  {
    title: 'Variation complete',
    path: 'confirmation',
    loadComponent: () =>
      import('../confirmation/confirmation.component').then(
        (c) => c.NotifyOperatorRegulatorLedVariationConfirmationComponent,
      ),
    data: { breadcrumb: false },
  },
];

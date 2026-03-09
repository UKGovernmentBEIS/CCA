import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { BaselineEnergyDraftService, FacilityWizardStep } from '@requests/common';

export const BASELINE_ENERGY_CONSUMPTION_ROUTES: Routes = [
  {
    path: '',
    providers: [BaselineEnergyDraftService],
    canDeactivate: [
      () => {
        inject(BaselineEnergyDraftService).clear();
        return true;
      },
    ],
    children: [
      {
        path: '',
        title: 'Baseline energy consumption',
        data: { backlink: `../${FacilityWizardStep.BASELINE_DATA}`, breadcrumb: false },
        loadComponent: () =>
          import('./baseline-energy-consumption.component').then((c) => c.BaselineEnergyConsumptionComponent),
      },
      {
        path: 'add-product',
        title: 'Add product',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('./add-product/add-product.component').then((c) => c.AddProductComponent),
      },
      {
        path: 'delete-product/:productName',
        title: 'Delete product',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./delete-product/delete-product.component').then((c) => c.DeleteProductComponent),
      },
    ],
  },
];

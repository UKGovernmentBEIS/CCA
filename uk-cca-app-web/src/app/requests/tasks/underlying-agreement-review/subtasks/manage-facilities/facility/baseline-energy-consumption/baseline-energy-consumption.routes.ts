import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { BaselineEnergyDraftService, FacilityWizardStep } from '@requests/common';

export const BASELINE_ENERGY_CONSUMPTION_ROUTES: Routes = [
  {
    path: '',
    canDeactivate: [
      () => {
        inject(BaselineEnergyDraftService).clear();
        return true;
      },
    ],
    children: [
      {
        title: 'Baseline energy consumption',
        path: '',
        loadComponent: () =>
          import('./baseline-energy-consumption.component').then((c) => c.BaselineEnergyConsumptionComponent),
        data: { backlink: `../${FacilityWizardStep.BASELINE_DATA}`, breadcrumb: false },
      },
      {
        title: 'Add product',
        path: 'add-product',
        loadComponent: () => import('./add-product/add-product.component').then((c) => c.AddProductComponent),
        data: { backlink: '../', breadcrumb: false },
      },
      {
        title: 'Delete product',
        path: 'delete-product/:productName',
        loadComponent: () => import('./delete-product/delete-product.component').then((c) => c.DeleteProductComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
    ],
    providers: [BaselineEnergyDraftService],
  },
];

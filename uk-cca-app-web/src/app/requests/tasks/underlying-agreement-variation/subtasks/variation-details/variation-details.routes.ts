import { Routes } from '@angular/router';

import { VariationDetailsWizardStep } from '@requests/common';

import {
  CanActivateVariationDetails,
  CanActivateVariationDetailsCheckYourAnswers,
  CanActivateVariationDetailsSummary,
} from './variation-details.guard';

export const VARIATION_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: VariationDetailsWizardStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateVariationDetailsSummary],
        loadComponent: () => import('./summary/variation-details-summary.component'),
      },
      {
        path: VariationDetailsWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateVariationDetailsCheckYourAnswers],
        loadComponent: () => import('./check-your-answers/variation-details-check-your-answers.component'),
      },
      {
        path: VariationDetailsWizardStep.DETAILS,
        title: 'Describe the changes',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateVariationDetails],
        loadComponent: () => import('@requests/common').then((m) => m.VariationDetailsComponent),
      },
      {
        path: '**',
        redirectTo: VariationDetailsWizardStep.SUMMARY,
      },
    ],
  },
];

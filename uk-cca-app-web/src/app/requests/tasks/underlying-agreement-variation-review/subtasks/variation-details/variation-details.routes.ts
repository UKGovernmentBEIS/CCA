import { Routes } from '@angular/router';

import { VariationDetailsReviewWizardStep } from '@requests/common';

import {
  canActivateVariationDetails,
  canActivateVariationDetailsCheckYourAnswers,
  canActivateVariationDetailsDecision,
  canActivateVariationDetailsSummary,
} from './variation-details.guard';

export const VARIATION_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: VariationDetailsReviewWizardStep.DECISION,
        title: 'Variation details',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateVariationDetailsDecision],
        loadComponent: () =>
          import('./decision/variation-details-decision.component').then((c) => c.VariationDetailsDecisionComponent),
      },
      {
        path: VariationDetailsReviewWizardStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateVariationDetailsSummary],
        loadComponent: () => import('./summary/variation-details-summary.component'),
      },
      {
        path: VariationDetailsReviewWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateVariationDetailsCheckYourAnswers],
        loadComponent: () => import('./check-your-answers/variation-details-check-your-answers.component'),
      },
      {
        path: VariationDetailsReviewWizardStep.DETAILS,
        title: 'Variation details',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [canActivateVariationDetails],
        loadComponent: () => import('@requests/common').then((m) => m.VariationDetailsComponent),
      },
      {
        path: '**',
        redirectTo: VariationDetailsReviewWizardStep.DECISION,
      },
    ],
  },
];

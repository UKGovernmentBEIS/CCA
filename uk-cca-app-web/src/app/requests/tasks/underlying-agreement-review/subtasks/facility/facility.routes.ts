import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { FacilityWizardReviewStep, isEditableSummaryRedirectGuard, underlyingAgreementQuery } from '@requests/common';

import {
  canActivateFacility,
  canActivateFacilityCheckYourAnswers,
  canActivateFacilityDecision,
  canActivateFacilitySummary,
} from './facility.guard';

export const FACILITY_ROUTES: Routes = [
  {
    path: ':facilityId',
    canActivate: [isEditableSummaryRedirectGuard],
    children: [
      {
        path: FacilityWizardReviewStep.DETAILS,
        title: 'Add facility details',
        data: { backlink: '../../../../', breadcrumb: false },
        canActivate: [canActivateFacility],
        loadComponent: () => import('@requests/common').then((c) => c.FacilityDetailsReviewComponent),
      },
      {
        path: FacilityWizardReviewStep.CONTACT_DETAILS,
        title: 'Add facility contact details',
        data: { backlink: `../${FacilityWizardReviewStep.DETAILS}`, breadcrumb: false },
        canActivate: [canActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityContactDetailsComponent),
      },
      {
        path: FacilityWizardReviewStep.ELIGIBILITY_DETAILS,
        title: 'Add CCA eligibility details and authorisation',
        data: { backlink: `../${FacilityWizardReviewStep.CONTACT_DETAILS}`, breadcrumb: false },
        canActivate: [canActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityEligibilityDetailsComponent),
      },
      {
        path: FacilityWizardReviewStep.EXTENT,
        title: 'Extent of the facility',
        data: { backlink: `../${FacilityWizardReviewStep.ELIGIBILITY_DETAILS}`, breadcrumb: false },
        canActivate: [canActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityExtentComponent),
      },
      {
        path: FacilityWizardReviewStep.APPLY_RULE,
        title: 'Apply the 70% rule',
        data: { backlink: `../${FacilityWizardReviewStep.EXTENT}`, breadcrumb: false },
        canActivate: [canActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityApplyRuleComponent),
      },
      {
        path: FacilityWizardReviewStep.DECISION,
        title: (route: ActivatedRouteSnapshot) => {
          const store = inject(RequestTaskStore);
          const facility = store.select(underlyingAgreementQuery.selectFacility(route.params.facilityId))();
          return facility.facilityDetails.name;
        },
        data: { backlink: '../../../../', breadcrumb: false },
        canActivate: [canActivateFacilityDecision],
        loadComponent: () => import('./decision/facility-decision.component').then((c) => c.FacilityDecisionComponent),
      },
      {
        path: FacilityWizardReviewStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../../', breadcrumb: false },
        canActivate: [canActivateFacilityCheckYourAnswers],
        loadComponent: () => import('./check-answers/facility-check-answers.component'),
      },
      {
        path: FacilityWizardReviewStep.SUMMARY,
        title: 'Summary details',
        data: { backlink: '../../../../', breadcrumb: false },
        canActivate: [canActivateFacilitySummary],
        loadComponent: () => import('./summary/facility-summary.component'),
      },
      {
        path: '**',
        redirectTo: FacilityWizardReviewStep.DECISION,
      },
    ],
  },
];

import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  isEditableSummaryRedirectGuard,
  resetCurrentFacility,
  setCurrentFacility,
  underlyingAgreementQuery,
} from '@requests/common';

import { facilityRedirectGuard } from './facility-redirect.guard';

export const FACILITY_ROUTES: Routes = [
  {
    path: ':facilityId',
    canActivate: [isEditableSummaryRedirectGuard, setCurrentFacility],
    canDeactivate: [resetCurrentFacility],
    children: [
      {
        path: '',
        canActivate: [facilityRedirectGuard],
        children: [],
      },
      {
        path: FacilityWizardStep.DETAILS,
        title: 'Add facility details',
        data: { backlink: '../../../../', breadcrumb: false },
        loadComponent: () => import('./details/facility-details.component').then((c) => c.FacilityDetailsComponent),
      },
      {
        path: FacilityWizardStep.CONTACT_DETAILS,
        title: 'Add facility contact details',
        data: { backlink: `../${FacilityWizardStep.DETAILS}`, breadcrumb: false },
        loadComponent: () =>
          import('./contact-details/facility-contact-details.component').then((c) => c.FacilityContactDetailsComponent),
      },
      {
        path: FacilityWizardStep.ELIGIBILITY_DETAILS,
        title: 'Add CCA eligibility details and authorisation',
        data: { backlink: `../${FacilityWizardStep.CONTACT_DETAILS}`, breadcrumb: false },
        loadComponent: () =>
          import('./eligibility-details/facility-eligibility-details.component').then(
            (c) => c.FacilityEligibilityDetailsComponent,
          ),
      },
      {
        path: FacilityWizardStep.EXTENT,
        title: 'Extent of the facility',
        data: { backlink: `../${FacilityWizardStep.ELIGIBILITY_DETAILS}`, breadcrumb: false },
        loadComponent: () => import('./extent/facility-extent.component').then((c) => c.FacilityExtentComponent),
      },
      {
        path: FacilityWizardStep.APPLY_RULE,
        title: 'Apply the 70% rule',
        data: { backlink: `../${FacilityWizardStep.EXTENT}`, breadcrumb: false },
        loadComponent: () =>
          import('./apply-rule/facility-apply-rule.component').then((c) => c.FacilityApplyRuleComponent),
      },
      {
        path: 'decision',
        title: (route: ActivatedRouteSnapshot) => {
          const store = inject(RequestTaskStore);
          const facility = store.select(underlyingAgreementQuery.selectFacility(route.params.facilityId))();
          return facility.facilityDetails.name;
        },
        data: { backlink: '../../../../', breadcrumb: false },
        loadComponent: () => import('./decision/facility-decision.component').then((c) => c.FacilityDecisionComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../../', breadcrumb: false },
        loadComponent: () => import('./check-answers/facility-check-answers.component'),
      },
      {
        path: 'summary',
        title: 'Summary details',
        data: { backlink: '../../../../', breadcrumb: false },
        loadComponent: () => import('./summary/facility-summary.component'),
      },
    ],
  },
];

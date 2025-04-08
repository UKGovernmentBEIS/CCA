import { Routes } from '@angular/router';

import {
  CanActivateFacility,
  CanActivateFacilityCheckYourAnswers,
  CanActivateFacilitySummary,
  FacilityWizardStep,
  resetCurrentFacility,
  setCurrentFacility,
} from '@requests/common';

export const FACILITY_ROUTES: Routes = [
  {
    path: ':facilityId',
    canActivate: [setCurrentFacility],
    canDeactivate: [resetCurrentFacility],
    children: [
      {
        path: FacilityWizardStep.DETAILS,
        title: 'Add facility details',
        data: { backlink: '../../../../', breadcrumb: false },
        canActivate: [CanActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityDetailsComponent),
      },
      {
        path: FacilityWizardStep.CONTACT_DETAILS,
        title: 'Add facility contact details',
        data: { backlink: `../${FacilityWizardStep.DETAILS}`, breadcrumb: false },
        canActivate: [CanActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityContactDetailsComponent),
      },
      {
        path: FacilityWizardStep.ELIGIBILITY_DETAILS,
        title: 'Add CCA eligibility details and authorisation',
        data: { backlink: `../${FacilityWizardStep.CONTACT_DETAILS}`, breadcrumb: false },
        canActivate: [CanActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityEligibilityDetailsComponent),
      },
      {
        path: FacilityWizardStep.EXTENT,
        title: 'Extent of the facility',
        data: { backlink: `../${FacilityWizardStep.ELIGIBILITY_DETAILS}`, breadcrumb: false },
        canActivate: [CanActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityExtentComponent),
      },
      {
        path: FacilityWizardStep.APPLY_RULE,
        title: 'Apply the 70% rule',
        data: { backlink: `../${FacilityWizardStep.EXTENT}`, breadcrumb: false },
        canActivate: [CanActivateFacility],
        loadComponent: () => import('@requests/common').then((m) => m.FacilityApplyRuleComponent),
      },
      {
        path: FacilityWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../../', breadcrumb: false },
        canActivate: [CanActivateFacilityCheckYourAnswers],
        loadComponent: () => import('./check-answers/facility-check-answers.component'),
      },
      {
        path: FacilityWizardStep.SUMMARY,
        title: 'Summary details',
        data: { backlink: '../../../../', breadcrumb: false },
        canActivate: [CanActivateFacilitySummary],
        loadComponent: () => import('./summary/facility-summary.component'),
      },
      {
        path: '**',
        redirectTo: FacilityWizardStep.SUMMARY,
      },
    ],
  },
];

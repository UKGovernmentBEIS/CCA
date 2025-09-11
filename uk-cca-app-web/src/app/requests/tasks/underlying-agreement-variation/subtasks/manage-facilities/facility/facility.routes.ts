import { Routes } from '@angular/router';

import { FacilityWizardStep, resetCurrentFacility, setCurrentFacility } from '@requests/common';

import { facilityRedirectGuard } from './facility.guard';

export const FACILITY_ROUTES: Routes = [
  {
    path: '',
    canActivate: [setCurrentFacility],
    canDeactivate: [resetCurrentFacility],
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [facilityRedirectGuard],
        children: [],
      },
      {
        path: FacilityWizardStep.DETAILS,
        title: 'Facility details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./details/facility-details.component').then((m) => m.FacilityDetailsComponent),
      },
      {
        path: FacilityWizardStep.CONTACT_DETAILS,
        title: 'Facility contact details',
        data: { backlink: `../${FacilityWizardStep.DETAILS}`, breadcrumb: false },
        loadComponent: () =>
          import('./contact-details/facility-contact-details.component').then((m) => m.FacilityContactDetailsComponent),
      },
      {
        path: FacilityWizardStep.ELIGIBILITY_DETAILS,
        title: 'CCA eligibility details and authorisation',
        data: { backlink: `../${FacilityWizardStep.CONTACT_DETAILS}`, breadcrumb: false },
        loadComponent: () =>
          import('./eligibility-details/facility-eligibility-details.component').then(
            (m) => m.FacilityEligibilityDetailsComponent,
          ),
      },
      {
        path: FacilityWizardStep.EXTENT,
        title: 'Extent of the facility',
        data: { backlink: `../${FacilityWizardStep.ELIGIBILITY_DETAILS}`, breadcrumb: false },
        loadComponent: () => import('./extent/facility-extent.component').then((m) => m.FacilityExtentComponent),
      },
      {
        path: FacilityWizardStep.APPLY_RULE,
        title: 'Apply the 70% rule',
        data: { backlink: `../${FacilityWizardStep.EXTENT}`, breadcrumb: false },
        loadComponent: () =>
          import('./apply-rule/facility-apply-rule.component').then((m) => m.FacilityApplyRuleComponent),
      },
      {
        path: 'summary',
        title: 'Summary details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./summary/facility-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./check-answers/facility-check-answers.component'),
      },
    ],
  },
];

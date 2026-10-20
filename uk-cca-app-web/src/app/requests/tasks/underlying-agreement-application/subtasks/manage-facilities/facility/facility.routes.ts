import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  FacilityWizardStep,
  resetCurrentFacility,
  setCurrentFacility,
  TaskItemStatus,
  underlyingAgreementQuery,
} from '@requests/common';

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
        loadComponent: () => import('./details/facility-details.component').then((c) => c.FacilityDetailsComponent),
      },
      {
        path: FacilityWizardStep.CONTACT_DETAILS,
        title: 'Facility contact details',
        data: { backlink: `../${FacilityWizardStep.DETAILS}`, breadcrumb: false },
        loadComponent: () =>
          import('./contact-details/facility-contact-details.component').then((c) => c.FacilityContactDetailsComponent),
      },
      {
        path: FacilityWizardStep.ELIGIBILITY_DETAILS,
        title: 'CCA eligibility details and authorisation',
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
        path: FacilityWizardStep.TARGET_COMPOSITION,
        title: 'Target composition',
        data: { backlink: `../${FacilityWizardStep.APPLY_RULE}`, breadcrumb: false },
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
      },
      {
        path: FacilityWizardStep.BASELINE_DATA,
        title: 'Baseline data',
        data: { backlink: `../${FacilityWizardStep.TARGET_COMPOSITION}`, breadcrumb: false },
        loadComponent: () => import('./baseline-data/baseline-data.component').then((c) => c.BaselineDataComponent),
      },
      {
        path: FacilityWizardStep.BASELINE_ENERGY_CONSUMPTION,
        data: { backlink: `../${FacilityWizardStep.BASELINE_DATA}`, breadcrumb: false },
        loadChildren: () =>
          import('./baseline-energy-consumption/baseline-energy-consumption.routes').then(
            (r) => r.BASELINE_ENERGY_CONSUMPTION_ROUTES,
          ),
      },
      {
        path: FacilityWizardStep.TARGETS,
        title: 'Targets',
        data: { backlink: `../${FacilityWizardStep.BASELINE_ENERGY_CONSUMPTION}`, breadcrumb: false },
        loadComponent: () => import('./targets/targets.component').then((c) => c.TargetsComponent),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./check-answers/facility-check-answers.component'),
      },
      {
        path: 'products',
        title: 'View Products',
        data: {
          breadcrumb: false,
          backlink: ({ sectionStatus }) =>
            sectionStatus === TaskItemStatus.COMPLETED ? '../summary' : '../check-your-answers',
        },
        resolve: {
          sectionStatus: (route: ActivatedRouteSnapshot) => {
            const store = inject(RequestTaskStore);
            const facilityId = route.paramMap.get('facilityId');
            const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();
            return sectionsCompleted?.[facilityId];
          },
        },
        loadComponent: () => import('@requests/common').then((m) => m.SummaryProductsComponent),
      },
      {
        path: 'summary',
        title: 'Summary details',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./summary/facility-summary.component'),
      },
    ],
  },
];

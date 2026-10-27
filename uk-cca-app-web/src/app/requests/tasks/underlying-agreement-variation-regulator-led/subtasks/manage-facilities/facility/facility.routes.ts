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
        title: 'Facility details',
        path: FacilityWizardStep.DETAILS,
        loadComponent: () => import('./details/facility-details.component').then((c) => c.FacilityDetailsComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Facility contact details',
        path: FacilityWizardStep.CONTACT_DETAILS,
        loadComponent: () =>
          import('./contact-details/facility-contact-details.component').then((c) => c.FacilityContactDetailsComponent),
        data: { backlink: `../${FacilityWizardStep.DETAILS}`, breadcrumb: false },
      },
      {
        title: 'CCA eligibility details and authorisation',
        path: FacilityWizardStep.ELIGIBILITY_DETAILS,
        loadComponent: () =>
          import('./eligibility-details/facility-eligibility-details.component').then(
            (c) => c.FacilityEligibilityDetailsComponent,
          ),
        data: { backlink: `../${FacilityWizardStep.CONTACT_DETAILS}`, breadcrumb: false },
      },
      {
        title: 'Extent of the facility',
        path: FacilityWizardStep.EXTENT,
        loadComponent: () => import('./extent/facility-extent.component').then((c) => c.FacilityExtentComponent),
        data: { backlink: `../${FacilityWizardStep.ELIGIBILITY_DETAILS}`, breadcrumb: false },
      },
      {
        title: 'Apply the 70% rule',
        path: FacilityWizardStep.APPLY_RULE,
        loadComponent: () =>
          import('./apply-rule/facility-apply-rule.component').then((c) => c.FacilityApplyRuleComponent),
        data: { backlink: `../${FacilityWizardStep.EXTENT}`, breadcrumb: false },
      },
      {
        title: 'Target composition',
        path: FacilityWizardStep.TARGET_COMPOSITION,
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
        data: { backlink: `../${FacilityWizardStep.APPLY_RULE}`, breadcrumb: false },
      },
      {
        title: 'Baseline data',
        path: FacilityWizardStep.BASELINE_DATA,
        loadComponent: () => import('./baseline-data/baseline-data.component').then((c) => c.BaselineDataComponent),
        data: { backlink: `../${FacilityWizardStep.TARGET_COMPOSITION}`, breadcrumb: false },
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
        title: 'Targets',
        path: FacilityWizardStep.TARGETS,
        loadComponent: () => import('./targets/targets.component').then((c) => c.TargetsComponent),
        data: { backlink: `../${FacilityWizardStep.BASELINE_ENERGY_CONSUMPTION}`, breadcrumb: false },
      },
      {
        title: 'View Products',
        path: FacilityWizardStep.PRODUCTS,
        loadComponent: () => import('@requests/common').then((c) => c.SummaryProductsComponent),
        data: {
          breadcrumb: false,
          backlink: ({ sectionStatus }: { sectionStatus: TaskItemStatus }) =>
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
      },
      {
        title: 'Summary details',
        path: 'summary',
        loadComponent: () => import('./summary/facility-summary.component'),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-answers/facility-check-answers.component'),
        data: { backlink: '../../', breadcrumb: false },
      },
    ],
  },
];

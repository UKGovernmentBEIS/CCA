import { Routes } from '@angular/router';

import { ReviewTargetUnitDetailsWizardStep } from '@requests/common';

import { OperatorAddressComponent } from './operator-address/operator-address.component';
import { ResponsiblePersonComponent } from './responsible-person/responsible-person.component';
import { reviewTargetUnitDetailsRedirectGuard } from './review-target-unit-details.guard';
import { TargetUnitDetailsComponent } from './target-unit-details/target-unit-details.component';

export const REVIEW_TARGET_UNIT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [reviewTargetUnitDetailsRedirectGuard],
        children: [],
      },
      {
        path: 'summary',
        title: 'Review target unit details',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/review-target-unit-details-summary.component'),
      },
      {
        path: 'decision',
        title: 'Target unit details',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./decision/review-target-unit-details-decision.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/review-target-unit-details-check-your-answers.component'),
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        title: 'Edit target unit details',
        data: { backlink: '../', breadcrumb: false },
        component: TargetUnitDetailsComponent,
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS,
        title: 'Edit operator address',
        data: { backlink: '../', breadcrumb: false },
        component: OperatorAddressComponent,
      },
      {
        path: ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON,
        title: 'Edit responsible person',
        data: { backlink: '../', breadcrumb: false },
        component: ResponsiblePersonComponent,
      },
    ],
  },
];

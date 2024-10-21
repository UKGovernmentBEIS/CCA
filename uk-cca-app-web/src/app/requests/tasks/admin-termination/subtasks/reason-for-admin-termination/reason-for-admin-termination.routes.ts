import { Routes } from '@angular/router';

import { ReasonForAdminTerminationWizardStep } from '../../admin-termination.types';
import {
  CanActivateReasonForAdminTerminationCheckYourAnswers,
  CanActivateReasonForAdminTerminationDetails,
  CanActivateReasonForAdminTerminationSummary,
} from './reason-for-admin-termination.guard';

export const REASON_FOR_ADMIN_TERMINATION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: ReasonForAdminTerminationWizardStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateReasonForAdminTerminationSummary],
        loadComponent: () => import('./summary/reason-for-admin-termination-summary.component'),
      },
      {
        path: ReasonForAdminTerminationWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateReasonForAdminTerminationCheckYourAnswers],
        loadComponent: () => import('./check-your-answers/reason-for-admin-termination-check-your-answers.component'),
      },
      {
        path: ReasonForAdminTerminationWizardStep.REASON_DETAILS,
        title: 'Admin termination reason details',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateReasonForAdminTerminationDetails],
        loadComponent: () => import('./reason-for-admin-termination.component'),
      },
      {
        path: '**',
        redirectTo: ReasonForAdminTerminationWizardStep.REASON_DETAILS,
      },
    ],
  },
];

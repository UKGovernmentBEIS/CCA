import { Routes } from '@angular/router';

import { ReasonForWithdrawAdminTerminationWizardStep } from '../../withdraw-admin-termination.types';
import {
  CanActivateReasonForWithdrawAdminTerminationCheckYourAnswers,
  CanActivateReasonForWithdrawAdminTerminationDetails,
  CanActivateReasonForWithdrawAdminTerminationSummary,
} from './reason-for-withdraw-admin-termination.guard';

export const REASON_FOR_WITHDRAW_ADMIN_TERMINATION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: ReasonForWithdrawAdminTerminationWizardStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateReasonForWithdrawAdminTerminationSummary],
        loadComponent: () => import('./summary/reason-for-withdraw-admin-termination-summary.component'),
      },
      {
        path: ReasonForWithdrawAdminTerminationWizardStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateReasonForWithdrawAdminTerminationCheckYourAnswers],
        loadComponent: () =>
          import('./check-your-answers/reason-for-withdraw-admin-termination-check-your-answers.component'),
      },
      {
        path: ReasonForWithdrawAdminTerminationWizardStep.REASON_DETAILS,
        title: 'Withdrawing admin termination reason details',
        data: { backlink: '../../../', breadcrumb: false },
        canActivate: [CanActivateReasonForWithdrawAdminTerminationDetails],
        loadComponent: () => import('./reason-for-withdraw-admin-termination.component'),
      },
      {
        path: '**',
        redirectTo: ReasonForWithdrawAdminTerminationWizardStep.REASON_DETAILS,
      },
    ],
  },
];

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { ButtonDirective } from '@netz/govuk-components';

import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

import {
  ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
  AdminTerminationFinalDecisionTerminateAgreementWizardStep,
} from '../../../admin-termination-final-decision.helper';

@Component({
  selector: 'cca-final-decision-reason-actions',
  template: `
    <netz-page-heading>Admin termination final decision</netz-page-heading>
    <h3 class="govuk-heading-m">Available actions</h3>
    <p>You must select the admin termination final decision.</p>

    <div class="govuk-button-group">
      <button (click)="onSelect('TERMINATE_AGREEMENT')" netzPendingButton govukButton type="button">
        Terminate agreement
      </button>

      <button (click)="onSelect('WITHDRAW_TERMINATION')" govukSecondaryButton type="button">
        Withdraw termination
      </button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [PageHeadingComponent, ButtonDirective, PendingButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonActionsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly adminTerminationFinalDecisionTaskService = inject(TaskService);

  onSelect(action: AdminTerminationFinalDecisionReasonDetails['finalDecisionType']) {
    this.adminTerminationFinalDecisionTaskService
      .saveSubtask(
        ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
        AdminTerminationFinalDecisionTerminateAgreementWizardStep.ACTIONS,
        this.activatedRoute,
        { finalDecisionType: action },
      )
      .subscribe();
  }
}

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
  templateUrl: './final-decision-reason-actions.component.html',
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

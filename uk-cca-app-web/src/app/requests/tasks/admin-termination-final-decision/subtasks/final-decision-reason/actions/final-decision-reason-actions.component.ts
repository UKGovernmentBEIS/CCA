import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { produce } from 'immer';

import {
  AdminTerminationFinalDecisionReasonDetails,
  AdminTerminationFinalDecisionRequestTaskPayload,
  AdminTerminationFinalDecisionSaveRequestTaskActionPayload,
} from 'cca-api';

import { adminTerminationFinalDecisionQuery } from '../../../admin-termination-final-decision.selectors';
import { isWizardCompleted } from '../../../completed';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../../../types';

@Component({
  selector: 'cca-final-decision-reason-actions',
  template: `
    <netz-page-heading>Admin termination final decision</netz-page-heading>
    <h3 class="govuk-heading-m">Available actions</h3>
    <p>You must select the admin termination final decision.</p>

    <div class="govuk-button-group">
      <button (click)="onSubmit('TERMINATE_AGREEMENT')" netzPendingButton govukButton type="button">
        Terminate agreement
      </button>

      <button (click)="onSubmit('WITHDRAW_TERMINATION')" govukSecondaryButton type="button">
        Withdraw termination
      </button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, ButtonDirective, PendingButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonActionsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);

  onSubmit(action: AdminTerminationFinalDecisionReasonDetails['finalDecisionType']) {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as AdminTerminationFinalDecisionRequestTaskPayload;

    const updatedPayload = update(payload, action);
    const currentSectionsCompleted = this.requestTaskStore.select(
      adminTerminationFinalDecisionQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((response: AdminTerminationFinalDecisionRequestTaskPayload) => {
        const path = isWizardCompleted(response?.adminTerminationFinalDecisionReasonDetails)
          ? '../check-your-answers'
          : '../reason-details';

        this.router.navigate([path], { relativeTo: this.activatedRoute });
      });
  }
}

function update(
  payload: AdminTerminationFinalDecisionSaveRequestTaskActionPayload,
  action: AdminTerminationFinalDecisionReasonDetails['finalDecisionType'],
): AdminTerminationFinalDecisionSaveRequestTaskActionPayload {
  return produce(payload, (draft) => {
    draft.adminTerminationFinalDecisionReasonDetails = {
      ...draft.adminTerminationFinalDecisionReasonDetails,
      finalDecisionType: action,
    };
  });
}

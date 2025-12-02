import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DateInputComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TextareaComponent,
} from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, TRACK_CORRECTIVE_ACTION_SUBTASK } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { AuditTrackCorrectiveActionsSaveRequestTaskActionPayload, TargetUnitAccountInfoViewService } from 'cca-api';

import { trackCorrectiveActionsQuery } from '../../../track-corrective-actions.selectors';
import { createSaveRequestTaskActionProcessDTO, toSaveRequestTaskPayload } from '../../../transform';
import {
  TRACK_CORRECTIVE_ACTION_DETAILS_FORM,
  TrackCorrectiveActionDetailsFormModel,
  TrackCorrectiveActionDetailsFormProvider,
} from './track-corrective-actions-details-form.provider';

@Component({
  selector: 'cca-track-corrective-actions-details',
  templateUrl: './track-corrective-actions-details.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    DatePipe,
    WizardStepComponent,
    DateInputComponent,
    MultipleFileInputComponent,
    SummaryListComponent,
    SummaryListRowKeyDirective,
    SummaryListRowDirective,
    SummaryListRowValueDirective,
    TextareaComponent,
  ],
  providers: [TrackCorrectiveActionDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrackCorrectiveActionsDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly targetUnitAccountInfoViewService = inject(TargetUnitAccountInfoViewService);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  protected readonly actionId = this.activatedRoute.snapshot.params.actionId;
  protected readonly today = new Date();

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly targetUnitAccountDetails = toSignal(
    this.targetUnitAccountInfoViewService.getTargetUnitAccountDetailsById(
      this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)()?.accountId,
    ),
  );

  protected readonly correctiveActionResponse = this.requestTaskStore.select(
    trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions,
  )()?.correctiveActionResponses[this.actionId];

  protected readonly form = inject<TrackCorrectiveActionDetailsFormModel>(TRACK_CORRECTIVE_ACTION_DETAILS_FORM);

  onSubmit() {
    const payload = this.requestTaskStore.select(trackCorrectiveActionsQuery.selectPayload)();
    const savePayload = toSaveRequestTaskPayload(payload, this.actionId);
    const updatedPayload = update(savePayload, this.form);

    const currentSectionsCompleted = this.requestTaskStore.select(
      trackCorrectiveActionsQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[`${TRACK_CORRECTIVE_ACTION_SUBTASK}${this.actionId}`] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSaveRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AuditTrackCorrectiveActionsSaveRequestTaskActionPayload,
  form: TrackCorrectiveActionDetailsFormModel,
): AuditTrackCorrectiveActionsSaveRequestTaskActionPayload {
  return produce(payload, (draft) => ({
    ...draft,
    correctiveActionFollowUpResponse: {
      ...draft.correctiveActionFollowUpResponse,
      actionCarriedOutDate: form.value.actionCarriedOutDate?.toISOString(),
      comments: form.value.comments,
      evidenceFiles: fileUtils.toUUIDs(form.value.evidenceFiles),
    },
  }));
}

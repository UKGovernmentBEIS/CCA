import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TextareaComponent,
} from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, TRACK_CORRECTIVE_ACTION_SUBTASK } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  AuditTrackCorrectiveActionsSaveRequestTaskActionPayload,
  CorrectiveActionFollowUpResponse,
  FacilityHeaderInfoDTO,
  TasksService,
} from 'cca-api';

import { trackCorrectiveActionsQuery } from '../../../track-corrective-actions.selectors';
import { createSaveRequestTaskActionProcessDTO, toSaveRequestTaskPayload } from '../../../transform';
import {
  TRACK_CORRECTIVE_ACTION_IS_CARRIED_OUT_FORM,
  TrackCorrectiveActionIsCarriedOutFormModel,
  TrackCorrectiveActionIsCarriedOutFormProvider,
} from './track-corrective-actions-is-carried-out-form.provider';

@Component({
  selector: 'cca-track-corrective-actions-is-carried-out',
  templateUrl: './track-corrective-actions-is-carried-out.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    DatePipe,
    WizardStepComponent,
    RadioOptionComponent,
    RadioComponent,
    SummaryListComponent,
    SummaryListRowKeyDirective,
    SummaryListRowDirective,
    SummaryListRowValueDirective,
    ConditionalContentDirective,
    TextareaComponent,
  ],
  providers: [TrackCorrectiveActionIsCarriedOutFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrackCorrectiveActionsIsCarriedOutComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);

  protected readonly actionId = this.activatedRoute.snapshot.params.actionId;

  private readonly requestInfo = this.requestTaskStore.select(requestTaskQuery.selectRequestInfo);
  private readonly resourceType = computed(() => this.requestInfo()?.resourceType);
  private readonly resource = computed(() => this.requestInfo()?.resources?.[this.resourceType()]);

  protected readonly facilityInfo: Signal<FacilityHeaderInfoDTO> = toSignal(
    this.tasksService.getRequestTaskHeaderInfo(this.resourceType(), this.resource()),
  );

  protected readonly correctiveActionResponse = this.requestTaskStore.select(
    trackCorrectiveActionsQuery.selectAuditTrackCorrectiveActions,
  )()?.correctiveActionResponses[this.actionId];

  protected readonly form = inject<TrackCorrectiveActionIsCarriedOutFormModel>(
    TRACK_CORRECTIVE_ACTION_IS_CARRIED_OUT_FORM,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(trackCorrectiveActionsQuery.selectPayload)();
    const savePayload = toSaveRequestTaskPayload(payload, this.actionId);
    const updatedPayload = update(savePayload, this.form, this.actionId);

    const currentSectionsCompleted = this.requestTaskStore.select(
      trackCorrectiveActionsQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[`${TRACK_CORRECTIVE_ACTION_SUBTASK}${this.actionId}`] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSaveRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const path = this.form.value.isActionCarriedOut ? '../details' : '../check-your-answers';
      this.router.navigate([path], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AuditTrackCorrectiveActionsSaveRequestTaskActionPayload,
  form: FormGroup<{ isActionCarriedOut: FormControl<boolean>; comments: FormControl<string | null> }>,
  actionId: string,
): AuditTrackCorrectiveActionsSaveRequestTaskActionPayload {
  return produce(payload, (draft) => {
    const action: CorrectiveActionFollowUpResponse = {
      isActionCarriedOut: form.value.isActionCarriedOut,
      comments: form.value.isActionCarriedOut ? null : form.value.comments,
      actionCarriedOutDate: form.value.isActionCarriedOut
        ? (draft.correctiveActionFollowUpResponse?.actionCarriedOutDate ?? null)
        : null,
      evidenceFiles: form.value.isActionCarriedOut ? (draft.correctiveActionFollowUpResponse?.evidenceFiles ?? []) : [],
    };

    return {
      ...draft,
      actionTitle: actionId,
      correctiveActionFollowUpResponse: action,
    };
  });
}

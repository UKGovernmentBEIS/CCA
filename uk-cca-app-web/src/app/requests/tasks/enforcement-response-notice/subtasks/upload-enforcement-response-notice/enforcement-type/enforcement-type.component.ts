import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  NonComplianceEnforcementResponseNotice,
  NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload,
  NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
} from 'cca-api';

import { enforcementResponseNoticeQuery } from '../../../enforcement-response-notice.selectors';
import { UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK } from '../../../enforcement-response-notice.types';
import { createRequestTaskActionProcessDTO } from '../../../transform';

type EnforcementResponseNoticeTypeForm = FormGroup<{
  type: FormControl<NonComplianceEnforcementResponseNotice['type'] | null>;
}>;

@Component({
  selector: 'cca-enforcement-response-notice-enforcement-type',
  template: `
    <div class="govuk-!-width-two-thirds">
      <cca-wizard-step
        (formSubmit)="onSubmit()"
        [formGroup]="form()"
        caption="Enforcement response notice"
        heading="What type of enforcement response notice would you like to send?"
        data-testid="enforcement-response-notice-type-form"
      >
        <div class="govuk-!-margin-bottom-9" formControlName="type" govuk-radio>
          <govuk-radio-option value="PENALTY" label="Penalty notice" />

          @if (!isPenaltyReissue()) {
            <govuk-radio-option value="PENALTY_WAIVER" label="Penalty waiver notice" />
          }
        </div>
      </cca-wizard-step>

      <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
      <netz-return-to-task-or-action-page />
    </div>
  `,
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    RadioComponent,
    RadioOptionComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class EnforcementTypeComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly enforcementResponseNotice = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectEnforcementResponseNotice,
  );

  protected readonly isPenaltyReissue = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectIsPenaltyReissue,
  );

  protected readonly form = computed(
    () =>
      new FormGroup({
        type: new FormControl(this.enforcementResponseNotice()?.type ?? null, [
          GovukValidators.required('Select the type of enforcement response notice you would like to send'),
        ]),
      }) as EnforcementResponseNoticeTypeForm,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(enforcementResponseNoticeQuery.selectPayload)();
    const updatedPayload = update(payload, this.form());
    const currentSectionsCompleted = this.requestTaskStore.select(
      enforcementResponseNoticeQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const path = updatedPayload.enforcementResponseNotice?.file ? '../check-your-answers' : '../upload-notice';
      this.router.navigate([path], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
  form: EnforcementResponseNoticeTypeForm,
): NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload {
  const type = form.controls.type.value as NonComplianceEnforcementResponseNotice['type'];

  return produce(
    { enforcementResponseNotice: payload?.enforcementResponseNotice },
    (draft: Pick<NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload, 'enforcementResponseNotice'>) => {
      draft.enforcementResponseNotice = {
        ...draft.enforcementResponseNotice,
        type,
      } as NonComplianceEnforcementResponseNotice;
    },
  );
}

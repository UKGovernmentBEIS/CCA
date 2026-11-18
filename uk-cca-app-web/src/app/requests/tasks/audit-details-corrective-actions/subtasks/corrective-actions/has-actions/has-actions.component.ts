import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { AuditDetailsCorrectiveActionsSubmitRequestTaskPayload } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { CORRECTIVE_ACTIONS_SUBTASK } from '../../../types';

@Component({
  selector: 'cca-has-actions',
  template: `
    <div class="govuk-!-width-two-thirds">
      <cca-wizard-step
        (formSubmit)="onSubmit()"
        [formGroup]="form()"
        heading="Did the final audit report identify any corrective actions the operator must complete?"
        caption="Corrective actions"
        data-testid="has-actions-form"
      >
        <div class="govuk-!-margin-bottom-9" formControlName="hasActions" govuk-radio>
          <govuk-radio-option [value]="true" [label]="'Yes'" />
          <govuk-radio-option [value]="false" [label]="'No'" />
        </div>
      </cca-wizard-step>

      <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
      <netz-return-to-task-or-action-page />
    </div>
  `,
  imports: [
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    WizardStepComponent,
    RadioOptionComponent,
    RadioComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HasActionsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly auditDetailsAndCorrectiveActions = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions,
  );

  protected readonly form = computed(
    () =>
      new FormGroup({
        hasActions: new FormControl(this.auditDetailsAndCorrectiveActions()?.correctiveActions?.hasActions ?? null, [
          GovukValidators.required('Select yes if there are corrective actions the operator must complete'),
        ]),
      }),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(auditDetailsCorrectiveActionsQuery.selectPayload)();
    const updatedPayload = update(payload, this.form());

    const currentSectionsCompleted = this.requestTaskStore.select(
      auditDetailsCorrectiveActionsQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[CORRECTIVE_ACTIONS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const path = this.form().value.hasActions ? '../actions' : '../check-your-answers';
      this.router.navigate([path], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AuditDetailsCorrectiveActionsSubmitRequestTaskPayload,
  form: FormGroup<{ hasActions: FormControl<boolean> }>,
): AuditDetailsCorrectiveActionsSubmitRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.auditDetailsAndCorrectiveActions = {
      ...draft?.auditDetailsAndCorrectiveActions,
      correctiveActions: {
        hasActions: form.value.hasActions,
        actions: form.value.hasActions
          ? (draft.auditDetailsAndCorrectiveActions?.correctiveActions?.actions ?? [])
          : [],
      },
    };
  });
}

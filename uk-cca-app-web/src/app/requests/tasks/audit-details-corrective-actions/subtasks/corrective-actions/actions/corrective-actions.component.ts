import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { AuditDetailsCorrectiveActionsSubmitRequestTaskPayload } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { CORRECTIVE_ACTIONS_SUBTASK } from '../../../types';
import { ActionItemComponent } from './action-item/action-item.component';
import {
  CORRECTIVE_ACTIONS_FORM,
  CorrectiveActionFormModel,
  CorrectiveActionsFormModel,
  correctiveActionsFormProvider,
  createCorrectiveActionFormGroup,
} from './corrective-actions-form.provider';

@Component({
  selector: 'cca-corrective-actions',
  templateUrl: './corrective-actions.component.html',
  imports: [ReactiveFormsModule, ReturnToTaskOrActionPageComponent, WizardStepComponent, ActionItemComponent],
  providers: [correctiveActionsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CorrectiveActionsComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<CorrectiveActionsFormModel>(CORRECTIVE_ACTIONS_FORM);

  get correctiveActions(): FormArray<CorrectiveActionFormModel> {
    return this.form.controls.correctiveActions as FormArray<CorrectiveActionFormModel>;
  }

  onAddAction() {
    this.correctiveActions.push(createCorrectiveActionFormGroup(this.formBuilder));
  }

  onRemoveAction(index: number) {
    if (this.correctiveActions.length <= 1) return;
    this.correctiveActions.removeAt(index);
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(auditDetailsCorrectiveActionsQuery.selectPayload)();
    const updatedPayload = update(payload, this.form);

    const currentSectionsCompleted = this.requestTaskStore.select(
      auditDetailsCorrectiveActionsQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[CORRECTIVE_ACTIONS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AuditDetailsCorrectiveActionsSubmitRequestTaskPayload,
  form: CorrectiveActionsFormModel,
): AuditDetailsCorrectiveActionsSubmitRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.auditDetailsAndCorrectiveActions = {
      ...draft?.auditDetailsAndCorrectiveActions,
      correctiveActions: {
        ...draft?.auditDetailsAndCorrectiveActions?.correctiveActions,
        actions:
          form.value.correctiveActions.map((ca, index) => ({
            title: `${index + 1}`,
            details: ca.details,
            deadline: ca.deadline.toISOString(),
          })) ?? [],
      },
    };
  });
}

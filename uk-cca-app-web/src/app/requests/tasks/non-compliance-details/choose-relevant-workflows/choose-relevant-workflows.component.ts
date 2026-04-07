import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { NonComplianceDetails, RequestTaskActionPayload } from 'cca-api';

import { isNonComplianceWizardCompleted } from '../non-compliance-details.guard';
import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';
import { transformWorkflowLabel } from '../transform-workflow-label';
import { NON_COMPLIANCE_DETAILS_SUBTASK, NonComplianceDetailsPayload } from '../types';
import {
  CHOOSE_RELEVANT_WORKFLOWS_FORM,
  ChooseRelevantWorkflowsFormModel,
  ChooseRelevantWorkflowsFormProvider,
  createWorkflowFormControl,
  WorkflowFormControl,
} from './choose-relevant-workflows-form.provider';

@Component({
  selector: 'cca-choose-relevant-workflows',
  imports: [ReactiveFormsModule, WizardStepComponent, SelectComponent, ReturnToTaskOrActionPageComponent],
  providers: [ChooseRelevantWorkflowsFormProvider],
  templateUrl: './choose-relevant-workflows.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChooseRelevantWorkflowsComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<ChooseRelevantWorkflowsFormModel>(CHOOSE_RELEVANT_WORKFLOWS_FORM);
  protected readonly allWorkflows =
    this.requestTaskStore.select(nonComplianceDetailsQuery.selectAllRelevantWorkflows)() ?? {};
  private readonly workflowIds = Object.keys(this.allWorkflows);
  private readonly workflowIdsSet = new Set(this.workflowIds);
  private readonly workflowOptionsById = new Map<string, GovukSelectOption<string | null>>(
    this.workflowIds.map((workflowId) => [
      workflowId,
      {
        value: workflowId,
        text: `${workflowId} - ${transformWorkflowLabel(this.allWorkflows[workflowId])}`,
      },
    ]),
  );

  get workflows(): FormArray<WorkflowFormControl> {
    return this.form.controls.workflows;
  }

  getOptionsForRow(index: number): GovukSelectOption<string | null>[] {
    const currentSelection = this.workflows.at(index)?.value;
    const selectedInOtherRows = this.workflows.controls
      .map((control, controlIndex) => (controlIndex === index ? null : control.value))
      .filter((workflowId): workflowId is string => !!workflowId);
    const selectedInOtherRowsSet = new Set(selectedInOtherRows);
    const workflowOptions = this.workflowIds
      .filter((workflowId) => workflowId === currentSelection || !selectedInOtherRowsSet.has(workflowId))
      .map((workflowId) => this.workflowOptionsById.get(workflowId)!);

    return [...workflowOptions];
  }

  onAddItem() {
    if (this.isAddItemDisabled()) {
      return;
    }

    this.workflows.push(createWorkflowFormControl(this.formBuilder));
  }

  onDeleteItem(workflowId: string | null, fallbackIndex: number) {
    const workflowIndex =
      workflowId == null ? fallbackIndex : this.workflows.controls.findIndex((control) => control.value === workflowId);

    if (workflowIndex >= 0) {
      this.workflows.removeAt(workflowIndex);
    }
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(
      nonComplianceDetailsQuery.selectPayload,
    )() as NonComplianceDetailsPayload;
    const relevantWorkflows = (this.form.value.workflows ?? []).filter(
      (workflowId): workflowId is string => !!workflowId,
    );

    const nonComplianceDetails: NonComplianceDetails = {
      ...payload.nonComplianceDetails,
      relevantWorkflows,
    };

    const currentSectionsCompleted = this.requestTaskStore.select(nonComplianceDetailsQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[NON_COMPLIANCE_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = {
      requestTaskId,
      requestTaskActionType: 'NON_COMPLIANCE_DETAILS_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_DETAILS_SAVE_PAYLOAD',
        nonComplianceDetails,
        sectionsCompleted,
      } as RequestTaskActionPayload,
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      if (isNonComplianceWizardCompleted(nonComplianceDetails)) {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../choose-relevant-facilities'], { relativeTo: this.activatedRoute });
      }
    });
  }

  isAddItemDisabled(): boolean {
    const selectedWorkflowIds = this.workflows.controls
      .map((control) => control.value)
      .filter((workflowId): workflowId is string => !!workflowId && this.workflowIdsSet.has(workflowId));

    return new Set(selectedWorkflowIds).size >= this.workflowIds.length;
  }

  get addItemLabel(): string {
    return this.workflows.length > 0 ? 'Add another item' : 'Add Item';
  }
}

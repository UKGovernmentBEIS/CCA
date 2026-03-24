import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { NonComplianceDetails, RequestTaskActionPayload, WorkflowFacilityDTO } from 'cca-api';

import { isNonComplianceWizardCompleted } from '../non-compliance-details.guard';
import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';
import { NON_COMPLIANCE_DETAILS_SUBTASK, NonComplianceDetailsPayload } from '../types';
import {
  CHOOSE_RELEVANT_FACILITIES_FORM,
  ChooseRelevantFacilitiesFormModel,
  ChooseRelevantFacilitiesFormProvider,
  createFacilityFormGroup,
  FacilityFormGroup,
} from './choose-relevant-facilities-form.provider';

@Component({
  selector: 'cca-choose-relevant-facilities',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    SelectComponent,
    TextInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ChooseRelevantFacilitiesFormProvider],
  templateUrl: './choose-relevant-facilities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChooseRelevantFacilitiesComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<ChooseRelevantFacilitiesFormModel>(CHOOSE_RELEVANT_FACILITIES_FORM);
  protected readonly allFacilities =
    this.requestTaskStore.select(nonComplianceDetailsQuery.selectAllRelevantFacilities)() ?? {};
  private readonly facilityIds = Object.keys(this.allFacilities);
  private readonly facilityIdsSet = new Set(this.facilityIds);
  private readonly facilityOptionsById = new Map<string, GovukSelectOption<string | null>>(
    this.facilityIds.map((facilityId) => [
      facilityId,
      {
        value: facilityId,
        text: this.allFacilities[facilityId],
      },
    ]),
  );

  get facilities(): FormArray<FacilityFormGroup> {
    return this.form.controls.facilities;
  }

  getOptionsForRow(index: number): GovukSelectOption<string | null>[] {
    const currentControl = this.facilities.at(index);
    const currentSelection = currentControl.controls.isHistorical.value
      ? null
      : currentControl.controls.facilityBusinessId.value;
    const selectedInOtherRows = this.facilities.controls
      .map((control, controlIndex) => {
        if (controlIndex === index || control.controls.isHistorical.value) {
          return null;
        }

        return control.controls.facilityBusinessId.value;
      })
      .filter((facilityId): facilityId is string => !!facilityId);
    const selectedInOtherRowsSet = new Set(selectedInOtherRows);
    const facilityOptions = this.facilityIds
      .filter((facilityId) => facilityId === currentSelection || !selectedInOtherRowsSet.has(facilityId))
      .map((facilityId) => this.facilityOptionsById.get(facilityId)!);

    return [...facilityOptions];
  }

  onAddFacility() {
    if (this.isAddFacilityDisabled()) {
      return;
    }

    this.facilities.push(createFacilityFormGroup(this.formBuilder, false));
  }

  onAddHistoricalFacility() {
    this.facilities.push(createFacilityFormGroup(this.formBuilder, true));
  }

  onDeleteItem(facilityBusinessId: string | null, isHistorical: boolean, fallbackIndex: number) {
    const facilityIndex =
      facilityBusinessId == null
        ? fallbackIndex
        : this.facilities.controls.findIndex(
            (control) =>
              control.controls.facilityBusinessId.value === facilityBusinessId &&
              control.controls.isHistorical.value === isHistorical,
          );

    if (facilityIndex >= 0) {
      this.facilities.removeAt(facilityIndex);
    }
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(
      nonComplianceDetailsQuery.selectPayload,
    )() as NonComplianceDetailsPayload;
    const relevantFacilities = this.form
      .getRawValue()
      .facilities.map((facility) => ({
        ...facility,
        facilityBusinessId: facility.facilityBusinessId?.trim() || null,
      }))
      .filter((facility) => !!facility.facilityBusinessId)
      .map(
        (facility) =>
          ({
            facilityBusinessId: facility.facilityBusinessId!,
            isHistorical: facility.isHistorical,
          }) satisfies WorkflowFacilityDTO,
      );

    const nonComplianceDetails: NonComplianceDetails = {
      ...payload.nonComplianceDetails,
      relevantFacilities,
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
        this.router.navigate(['../issue-enforcement'], { relativeTo: this.activatedRoute });
      }
    });
  }

  isAddFacilityDisabled(): boolean {
    const selectedFacilityIds = this.facilities.controls
      .filter((control) => !control.controls.isHistorical.value)
      .map((control) => control.controls.facilityBusinessId.value)
      .filter((facilityId): facilityId is string => !!facilityId && this.facilityIdsSet.has(facilityId));

    return new Set(selectedFacilityIds).size >= this.facilityIds.length;
  }
}

import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent } from '@netz/govuk-components';
import {
  OPERATOR_ASSENT_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { Facility, UnderlyingAgreementVariationRegulatorLedSavePayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import {
  EXCLUDE_FACILITY_FORM,
  FacilityItemExcludeFormModel,
  FacilityItemExcludeFormProvider,
} from './facility-item-exclude-form.provider';

@Component({
  selector: 'cca-facility-item-exclude',
  templateUrl: './facility-item-exclude.component.html',
  imports: [ReactiveFormsModule, WizardStepComponent, RouterLink, DateInputComponent],
  providers: [FacilityItemExcludeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityItemExcludeComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityItemExcludeFormModel>>(EXCLUDE_FACILITY_FORM);
  protected readonly facilityId = this.route.snapshot.params.facilityId;
  protected readonly facility: Signal<Facility> = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacility(this.facilityId),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const savePayload = toUnAVariationRegulatorLedSavePayload(payload);

    const updatedPayload = excludeFacility(
      savePayload,
      this.facilityId,
      this.form.value.excludedDate.toISOString().split('T')[0],
    );

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.COMPLETED;

      draft[OPERATOR_ASSENT_DECISION_SUBTASK] =
        draft[OPERATOR_ASSENT_DECISION_SUBTASK] !== TaskItemStatus.COMPLETED
          ? draft[OPERATOR_ASSENT_DECISION_SUBTASK]
          : TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.route });
    });
  }
}

function excludeFacility(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  facilityId: string,
  excludedDate: string,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  return produce(payload, (draft) => {
    draft.facilities = draft.facilities.map((f) =>
      f.facilityId === facilityId
        ? {
            ...f,
            status: 'EXCLUDED',
            excludedDate,
          }
        : f,
    );
  });
}

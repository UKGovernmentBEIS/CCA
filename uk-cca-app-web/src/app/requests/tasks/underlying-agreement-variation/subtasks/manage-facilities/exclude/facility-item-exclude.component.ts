import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, underlyingAgreementQuery } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  Facility,
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps, resetFacilityReviewSection } from '../../../utils';
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
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const savePayload = toUnderlyingAgreementVariationSavePayload(payload);

    const updatedPayload = excludeFacility(
      savePayload,
      this.facilityId,
      this.form.value.excludedDate.toISOString().split('T')[0],
    );

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);

    // The third argument (false) is for stating if the current facility is identical to the original.
    // Since here this does not make any sense, we explicitly set it to false.
    const resetedProps = resetFacilityReviewSection(reviewProps, this.facilityId, false);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, {
      ...reviewProps,
      ...resetedProps,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.route });
    });
  }
}

function excludeFacility(
  payload: UnderlyingAgreementVariationApplySavePayload,
  facilityId: string,
  excludedDate: string,
): UnderlyingAgreementVariationApplySavePayload {
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

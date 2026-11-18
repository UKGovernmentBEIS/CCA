import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CommonFacilityTargetCompositionComponent,
  FacilityTargetCompositionSubmitEvent,
  FacilityWizardStep,
  isCCA3FacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
  updateFacilityTargetComposition,
} from '@requests/common';
import { produce } from 'immer';

import {
  UnderlyingAgreementSubmitRequestTaskPayload,
  UnderlyingAgreementVariationRegulatorLedSavePayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../../transform';

@Component({
  selector: 'cca-target-composition',
  template: `<cca-common-facility-target-composition (submitted)="onSubmit($event)" />`,
  imports: [CommonFacilityTargetCompositionComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetCompositionComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  onSubmit(event: FacilityTargetCompositionSubmitEvent) {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);

    const updatedPayload = updateFacilityTargetComposition(
      actionPayload,
      event.form,
      event.facilityId,
    ) as UnderlyingAgreementVariationRegulatorLedSavePayload;

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[event.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.store.select(underlyingAgreementVariationRegulatorLedQuery.selectDetermination)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((updatedRequestTaskPayload: UnderlyingAgreementSubmitRequestTaskPayload) => {
        const facility = updatedRequestTaskPayload.underlyingAgreement.facilities.find(
          (f) => f.facilityId === event.facilityId,
        );
        if (isCCA3FacilityWizardCompleted(facility)) {
          this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
        } else {
          this.router.navigate(['../', FacilityWizardStep.BASELINE_DATA], { relativeTo: this.route });
        }
      });
  }
}

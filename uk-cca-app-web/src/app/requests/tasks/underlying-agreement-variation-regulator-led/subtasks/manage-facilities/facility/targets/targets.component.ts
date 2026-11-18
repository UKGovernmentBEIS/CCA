import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CommonFacilityTargetsComponent,
  FacilityTargetsSubmitEvent,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
  updateFacilityTargets,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementVariationRegulatorLedSavePayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../../transform';

@Component({
  selector: 'cca-targets',
  template: `<cca-common-facility-targets (submitted)="onSubmit($event)" />`,
  imports: [CommonFacilityTargetsComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  onSubmit(event: FacilityTargetsSubmitEvent) {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);
    const updatedPayload = updateFacilityTargets(
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

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}

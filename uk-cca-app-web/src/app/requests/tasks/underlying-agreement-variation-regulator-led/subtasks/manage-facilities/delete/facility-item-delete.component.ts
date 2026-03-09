import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';
import {
  OPERATOR_ASSENT_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { produce } from 'immer';

import { Facility, UnderlyingAgreementVariationRegulatorLedSavePayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';

@Component({
  selector: 'cca-facility-item-delete',
  templateUrl: './facility-item-delete.component.html',
  imports: [PageHeadingComponent, RouterLink, ButtonDirective, PendingButtonDirective, WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityItemDeleteComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  private readonly facilityId = this.route.snapshot.params.facilityId;
  protected readonly facility: Signal<Facility> = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacility(this.facilityId),
  );

  onDelete() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const savePayload = toUnAVariationRegulatorLedSavePayload(payload);
    const updatedPayload = deleteFacility(savePayload, this.facilityId);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      delete draft[this.facilityId];

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

function deleteFacility(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  facilityId: string,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  return produce(payload, (draft) => {
    if (draft.facilities && draft.facilities.length > 0) {
      const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
      if (facilityIndex >= 0) {
        const facility = draft.facilities[facilityIndex];

        // When deleting a NEW facility, we need to delete the Charge start date as well, if it exists, since it is no longer valid.
        // For more information about the business logic of Charge start date, see `CCA-2601`.
        if (draft.facilityChargeStartDateMap[facility.facilityId]) {
          delete draft.facilityChargeStartDateMap[facility.facilityId];
        }

        draft.facilities.splice(facilityIndex, 1);
      }
    }
  });
}

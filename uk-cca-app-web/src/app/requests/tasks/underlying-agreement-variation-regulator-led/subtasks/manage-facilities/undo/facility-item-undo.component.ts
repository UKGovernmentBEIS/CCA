import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  OPERATOR_ASSENT_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementVariationRegulatorLedSavePayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';

@Component({
  selector: 'cca-facility-item-undo',
  templateUrl: './facility-item-undo.component.html',
  imports: [PageHeadingComponent, RouterLink, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityItemUndoComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  private readonly facilityId = this.route.snapshot.params.facilityId;
  protected readonly facility = this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId));

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(
        requestTaskQuery.selectRequestTaskPayload,
      )() as UNAVariationRegulatorLedRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const savePayload = toUnAVariationRegulatorLedSavePayload(payload);

    const currentFacility = savePayload.facilities?.find((f) => f.facilityId === this.facilityId);
    const originalFacility = originalPayload?.underlyingAgreement?.facilities?.find(
      (f) => f.facilityId === this.facilityId,
    );

    const areIdentical = areEntitiesIdentical(
      resetFacilityNonComparisonFields(currentFacility),
      resetFacilityNonComparisonFields(originalFacility),
    );

    const updatedPayload = undoFacility(savePayload, this.facilityId);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = areIdentical ? TaskItemStatus.UNCHANGED : TaskItemStatus.COMPLETED;

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

function undoFacility(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  facilityId: string,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  return produce(payload, (draft) => {
    draft.facilities = draft.facilities.map((f) =>
      f.facilityId === facilityId
        ? {
            ...f,
            status: 'LIVE',
            excludedDate: null,
          }
        : f,
    );
  });
}

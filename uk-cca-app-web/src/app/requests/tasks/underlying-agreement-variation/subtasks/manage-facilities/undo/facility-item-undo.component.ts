import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { produce } from 'immer';

import {
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';

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
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(
        requestTaskQuery.selectRequestTaskPayload,
      )() as UnderlyingAgreementVariationSubmitRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const savePayload = toUnderlyingAgreementVariationSavePayload(payload);

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
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.route });
    });
  }
}

function undoFacility(
  payload: UnderlyingAgreementVariationApplySavePayload,
  facilityId: string,
): UnderlyingAgreementVariationApplySavePayload {
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

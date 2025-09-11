import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';
import { MANAGE_FACILITIES_SUBTASK, TasksApiService, underlyingAgreementQuery } from '@requests/common';
import { produce } from 'immer';

import {
  Facility,
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps, removeFacilityReviewSection } from '../../../utils';

@Component({
  selector: 'cca-facility-item-delete',
  templateUrl: './facility-item-delete.component.html',
  standalone: true,
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
    // Step 1: Get payload from store
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    // Step 2: Transform to save action payload
    const savePayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Step 3: Apply business logic transformations
    const updatedPayload = deleteFacility(savePayload, this.facilityId);

    // Update sections completed
    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[MANAGE_FACILITIES_SUBTASK] = 'IN_PROGRESS';
      delete draft[this.facilityId];
    });

    // Create and send DTO
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const resetedProps = removeFacilityReviewSection(reviewProps, this.facilityId);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, {
      ...reviewProps,
      ...resetedProps,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.route });
    });
  }
}

function deleteFacility(
  payload: UnderlyingAgreementVariationApplySavePayload,
  facilityId: string,
): UnderlyingAgreementVariationApplySavePayload {
  return produce(payload, (draft) => {
    if (draft.facilities && draft.facilities.length > 0) {
      const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
      if (facilityIndex >= 0) draft.facilities.splice(facilityIndex, 1);
    }
  });
}

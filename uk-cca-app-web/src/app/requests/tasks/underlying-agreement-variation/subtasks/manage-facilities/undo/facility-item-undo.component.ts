import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { MANAGE_FACILITIES_SUBTASK, TasksApiService, underlyingAgreementQuery } from '@requests/common';
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
  standalone: true,
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
    // Step 1: Get payload from store
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    // Step 2: Transform to save action payload
    const savePayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Step 3: Apply business logic transformations
    const updatedPayload = undoFacility(savePayload, this.facilityId);

    // Update sections completed
    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[MANAGE_FACILITIES_SUBTASK] = 'IN_PROGRESS';
    });

    // Create and send DTO
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);
    // we cannot reset the review section here because in this step we do not know if the change of the section
    // started from the facility wizard, or was simply an exclusion. Thus, we prefer to keep the review section as undecided for safety.
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

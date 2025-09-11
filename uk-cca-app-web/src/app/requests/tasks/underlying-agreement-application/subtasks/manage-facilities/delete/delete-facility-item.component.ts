import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';
import { TasksApiService, underlyingAgreementQuery } from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

@Component({
  selector: 'cca-delete-facility-item',
  templateUrl: './delete-facility-item.component.html',
  standalone: true,
  imports: [PageHeadingComponent, ButtonDirective, PendingButtonDirective, WarningTextComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteFacilityItemComponent {
  protected readonly store = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly router = inject(Router);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly facility = this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId));

  protected readonly facilityName = this.facility()?.facilityDetails?.name;

  onDelete() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = deleteFacility(actionPayload, this.facilityId);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      delete draft[this.facilityId];
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.activatedRoute });
    });
  }
}

function deleteFacility(
  payload: UnderlyingAgreementApplySavePayload,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    if (draft.facilities && draft.facilities.length > 0) {
      const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
      if (facilityIndex >= 0) draft.facilities.splice(facilityIndex, 1);
    }
  });
}

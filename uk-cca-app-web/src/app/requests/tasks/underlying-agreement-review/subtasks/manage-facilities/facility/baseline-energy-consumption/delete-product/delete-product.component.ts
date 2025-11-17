import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { WarningTextComponent } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../../../transform';
import { resetDetermination } from '../../../../../utils';

@Component({
  selector: 'cca-delete-product',
  template: `
    <netz-page-heading [caption]="facility()?.facilityDetails?.name">
      Are you sure you want to delete {{ this.productName }}?
    </netz-page-heading>

    <p>Your product and all its data will be deleted permanently</p>

    <govuk-warning-text assistiveText="">You will not be able to undo this action.</govuk-warning-text>
    <button (click)="onDelete()" class="govuk-button govuk-button--warning">Delete product</button>
  `,
  imports: [WarningTextComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteProductComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly productName = this.activatedRoute.snapshot.paramMap.get('productName');

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onDelete() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);

    const updatedPayload = deleteVariableEnergyProduct(actionPayload, this.productName, this.facilityId);

    const currentReviewSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const currDetermination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted: this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
    });
  }
}

function deleteVariableEnergyProduct(
  payload: UnderlyingAgreementApplySavePayload,
  productName: string,
  facilityId: string,
) {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId);
    if (facilityIndex === -1) return;

    const facility = draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityBaselineEnergyConsumption;

    const data = facility.variableEnergyConsumptionDataByProduct;

    const foundIndex = data.findIndex((p) => p.productName === productName);
    if (foundIndex === -1) return;

    data.splice(foundIndex, 1);
  });
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { WarningTextComponent } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  filterFieldsWithFalsyValues,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../../utils';

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
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = deleteVariableEnergyProduct(actionPayload, this.productName, this.facilityId);

    const originalFacility = originalPayload?.underlyingAgreement?.facilities?.find(
      (f) => f.facilityId === this.facilityId,
    );
    const currentFacility = updatedPayload.facilities?.find((f) => f.facilityId === this.facilityId);

    let areIdentical = false;

    if (originalFacility) {
      const resetOriginal = resetFacilityNonComparisonFields(originalFacility);
      const resetCurrent = resetFacilityNonComparisonFields(currentFacility);

      const filterOriginal = filterFieldsWithFalsyValues(resetOriginal);
      const filterCurrent = filterFieldsWithFalsyValues(resetCurrent);

      areIdentical = areEntitiesIdentical(filterCurrent, filterOriginal);
    }

    const currentDecisions = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
    )();
    const decisions = areIdentical ? deleteFacilityDecision(currentDecisions, this.facilityId) : currentDecisions;

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const currentReviewSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const determination = resetDetermination(
      this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)(),
    );

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)(),
      facilitiesReviewGroupDecisions: decisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
    });
  }
}

function deleteVariableEnergyProduct(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  productName: string | null,
  facilityId: string,
) {
  return produce(payload, (draft) => {
    if (!productName) return;

    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId);
    if (facilityIndex === -1) return;

    const facility = draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityBaselineEnergyConsumption;

    const data = facility?.variableEnergyConsumptionDataByProduct;
    if (!data) return;

    const foundIndex = data.findIndex((p) => p.productName === productName);
    if (foundIndex === -1) return;

    data.splice(foundIndex, 1);
  });
}

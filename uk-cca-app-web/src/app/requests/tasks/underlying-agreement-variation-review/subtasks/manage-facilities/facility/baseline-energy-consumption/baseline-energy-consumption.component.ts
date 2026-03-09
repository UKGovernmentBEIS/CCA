import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, TextInputComponent } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  BaselineEnergyDraftService,
  calculateFixedEnergy,
  calculateOtherYearsVariableEnergy,
  calculateTotalEnergy,
  calculateVariableEnergy,
  extractBaselineYear,
  FacilityBaselineEnergyConsumptionFormModel,
  FacilityWizardStep,
  filterFieldsWithFalsyValues,
  isCCA3FacilityWizardCompleted,
  MeasurementTypeToUnitPipe,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SplitByProductTableComponent } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  FacilityBaselineEnergyConsumption,
  ProductVariableEnergyConsumptionData,
  UnderlyingAgreementVariationReviewSavePayload,
} from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../utils';
import {
  FACILITY_BASELINE_ENERGY_CONSUMPTION_FORM,
  FacilityBaselineEnergyConsumptionFormProvider,
} from './baseline-energy-consumption-form.provider';

@Component({
  selector: 'cca-baseline-energy-consumption',
  templateUrl: './baseline-energy-consumption.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    RouterLink,
    DecimalPipe,
    SplitByProductTableComponent,
    MeasurementTypeToUnitPipe,
  ],
  providers: [FacilityBaselineEnergyConsumptionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineEnergyConsumptionComponent {
  private readonly decimalPipe = new DecimalPipe('en-GB');
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly draftService = inject(BaselineEnergyDraftService);

  protected readonly form = inject<FacilityBaselineEnergyConsumptionFormModel>(
    FACILITY_BASELINE_ENERGY_CONSUMPTION_FORM,
  );

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly una = this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
  private readonly facilityIndex = this.una.facilities?.findIndex((f) => f.facilityId === this.facilityId) ?? -1;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly baselineYear = extractBaselineYear(
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacilityBaselineData(this.facilityIndex))()
      .baselineDate,
  );

  protected readonly facilityThroughputUnit = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacilityTargetComposition(this.facilityIndex),
  )()?.measurementType;

  protected readonly facilityEnergyConsumption = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacilityBaselineEnergyConsumption(this.facilityIndex),
  );

  protected readonly products = computed(() => this.draftService.draftSignal()?.products ?? []);

  private readonly hasVariableEnergyValue = toSignal(this.form.controls.hasVariableEnergy.valueChanges, {
    initialValue: this.form.value.hasVariableEnergy,
  });

  private readonly variableEnergyTypeValue = toSignal(this.form.controls.variableEnergyType.valueChanges, {
    initialValue: this.form.value.variableEnergyType,
  });

  private readonly totalFixedEnergyValue = toSignal(this.form.controls.totalFixedEnergy.valueChanges, {
    initialValue: this.form.value.totalFixedEnergy,
  });

  private readonly baselineVariableEnergyValue = toSignal(this.form.controls.baselineVariableEnergy.valueChanges, {
    initialValue: this.form.value.baselineVariableEnergy,
  });

  private readonly totalThroughputValue = toSignal(this.form.controls.totalThroughput.valueChanges, {
    initialValue: this.form.value.totalThroughput,
  });

  protected readonly showVariableEnergyType = computed(() => this.hasVariableEnergyValue() === true);

  protected readonly showTotalsOnlyFields = computed(
    () => this.hasVariableEnergyValue() === true && this.variableEnergyTypeValue() === 'TOTALS',
  );

  protected readonly showSplitByProductFields = computed(
    () => this.hasVariableEnergyValue() === true && this.variableEnergyTypeValue() === 'BY_PRODUCT',
  );

  protected readonly showThroughputFields = computed(
    () =>
      this.hasVariableEnergyValue() === false ||
      (this.hasVariableEnergyValue() === true && this.variableEnergyTypeValue() === 'TOTALS'),
  );

  protected readonly calculatedFixedEnergy = computed(() => calculateFixedEnergy(this.totalFixedEnergyValue()));

  protected readonly calculatedVariableEnergy = computed(() =>
    calculateVariableEnergy(
      this.hasVariableEnergyValue(),
      this.variableEnergyTypeValue(),
      this.baselineVariableEnergyValue(),
      this.products(),
      this.baselineYear,
    ),
  );

  protected readonly calculatedTotalEnergy = computed(() =>
    calculateTotalEnergy(this.calculatedFixedEnergy(), this.calculatedVariableEnergy()),
  );

  protected readonly calculatedOtherYearsVariable = computed(() =>
    calculateOtherYearsVariableEnergy(this.products(), this.baselineYear, this.variableEnergyTypeValue()),
  );

  protected energyIntensityDisplay(): string | null {
    if (!this.showTotalsOnlyFields()) return null;

    const energy = Number(this.baselineVariableEnergyValue());
    const throughput = Number(this.totalThroughputValue());

    if (!throughput) return null;

    const intensity = energy / throughput;
    if (!Number.isFinite(intensity)) return null;

    const formatted = this.decimalPipe.transform(intensity, '1.0-7');
    return formatted ?? null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    // Get products from draft service
    const draftProducts = this.draftService.draftSignal()?.products ?? [];

    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = updateFacilityBaselineEnergyConsumption(
      actionPayload,
      this.form,
      this.facilityId,
      draftProducts,
    );

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

    const currDetermination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)(),
      facilitiesReviewGroupDecisions: decisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((updated: UNAVariationReviewRequestTaskPayload) => {
      this.draftService.clear(); // Clear draft on success

      const facility = updated.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);

      if (isCCA3FacilityWizardCompleted(facility)) {
        const targetPath = areIdentical ? '../check-your-answers' : '../decision';
        this.router.navigate([targetPath], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../', FacilityWizardStep.TARGETS], { relativeTo: this.activatedRoute });
      }
    });
  }

  onAddProduct() {
    // Save current form state to draft service (NOT to store)
    this.draftService.saveFormSnapshot({
      totalFixedEnergy: this.form.value.totalFixedEnergy,
    });
    // Navigation handled by routerLink in template
  }

  onDeleteProduct(product: ProductVariableEnergyConsumptionData) {
    this.router.navigate(['delete-product', product.productName], { relativeTo: this.activatedRoute });
  }
}

function updateFacilityBaselineEnergyConsumption(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FacilityBaselineEnergyConsumptionFormModel,
  facilityId: string,
  products: ProductVariableEnergyConsumptionData[] | null | undefined,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((facility) => facility.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const formValue = form.value;
    const hasVariableEnergy = formValue.hasVariableEnergy === true;
    const variableEnergyType = hasVariableEnergy ? (formValue.variableEnergyType ?? undefined) : undefined;

    const existingBaselineEnergy =
      draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityBaselineEnergyConsumption;

    const updatedBaselineEnergy: FacilityBaselineEnergyConsumption = {
      totalFixedEnergy: calculateFixedEnergy(formValue.totalFixedEnergy),
      hasVariableEnergy,
    };

    if (hasVariableEnergy) {
      if (variableEnergyType === 'TOTALS') {
        updatedBaselineEnergy.variableEnergyType = 'TOTALS';
        updatedBaselineEnergy.baselineVariableEnergy = formValue.baselineVariableEnergy;
        updatedBaselineEnergy.totalThroughput = formValue.totalThroughput;
        updatedBaselineEnergy.throughputUnit = formValue.throughputUnit;
        updatedBaselineEnergy.variableEnergyConsumptionDataByProduct = [];
      } else {
        updatedBaselineEnergy.variableEnergyType = 'BY_PRODUCT';
        const persistedProducts = products ?? existingBaselineEnergy?.variableEnergyConsumptionDataByProduct ?? [];
        updatedBaselineEnergy.variableEnergyConsumptionDataByProduct = persistedProducts.map((product) => ({
          ...product,
        }));
      }
    } else {
      // When hasVariableEnergy is false, include throughput fields
      updatedBaselineEnergy.totalThroughput = formValue.totalThroughput;
      updatedBaselineEnergy.throughputUnit = formValue.throughputUnit;
    }

    draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityBaselineEnergyConsumption = updatedBaselineEnergy;
  });
}

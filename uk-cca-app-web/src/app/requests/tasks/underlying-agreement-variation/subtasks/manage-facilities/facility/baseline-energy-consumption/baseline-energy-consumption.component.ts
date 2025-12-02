import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, TextInputComponent } from '@netz/govuk-components';
import {
  calculateFixedEnergy,
  calculateOtherYearsVariableEnergy,
  calculateTotalEnergy,
  calculateVariableEnergy,
  extractBaselineYear,
  FacilityBaselineEnergyConsumptionFormModel,
  FacilityWizardStep,
  isCCA3FacilityWizardCompleted,
  MeasurementTypeToUnitPipe,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { SplitByProductTableComponent } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  FacilityBaselineEnergyConsumption,
  ProductVariableEnergyConsumptionData,
  UnderlyingAgreementSubmitRequestTaskPayload,
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../transform';
import { extractReviewProps } from '../../../../utils';
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

  protected readonly products = computed(
    () => this.facilityEnergyConsumption()?.variableEnergyConsumptionDataByProduct,
  );

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
    // units are rendered in template
    return formatted ?? null;
  }

  onSubmit() {
    if (this.form.invalid) return;

    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const updatedPayload = updateFacilityBaselineEnergyConsumption(
      actionPayload,
      this.form,
      this.facilityId,
      this.form.controls.products.value ?? this.products(),
    );

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((updated: UnderlyingAgreementSubmitRequestTaskPayload) => {
        const facility = updated.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);

        if (isCCA3FacilityWizardCompleted(facility)) {
          this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
        } else {
          this.router.navigate(['../', FacilityWizardStep.TARGETS], { relativeTo: this.activatedRoute });
        }
      });
  }

  onAddProduct() {
    const currentState = this.requestTaskStore.state;

    const updatedState = produce(currentState, (draft) => {
      const payload = draft.requestTaskItem.requestTask.payload as UnderlyingAgreementVariationSubmitRequestTaskPayload;
      const facilities = payload?.underlyingAgreement?.facilities;
      const facilityIndex = facilities.findIndex((facility) => facility.facilityId === this.facilityId);
      if (facilityIndex === -1) return;

      const baselineAndTargets = facilities[facilityIndex].cca3BaselineAndTargets;

      const formValue = this.form.getRawValue();
      const existingBaselineEnergy = baselineAndTargets?.facilityBaselineEnergyConsumption;

      const totalFixedEnergyValue = formValue.totalFixedEnergy ?? existingBaselineEnergy?.totalFixedEnergy ?? '';

      const products = this.products() ?? existingBaselineEnergy?.variableEnergyConsumptionDataByProduct ?? [];

      baselineAndTargets.facilityBaselineEnergyConsumption = {
        ...existingBaselineEnergy,
        totalFixedEnergy: String(totalFixedEnergyValue),
        // hard coded values are used here because the user can never add a product without
        // hasVariableEnergy: true && BY_PRODUCT being selected.
        hasVariableEnergy: true,
        variableEnergyType: 'BY_PRODUCT',
        variableEnergyConsumptionDataByProduct: products,
      };
    });

    this.requestTaskStore.setState(updatedState);
  }

  onDeleteProduct(product: ProductVariableEnergyConsumptionData) {
    this.router.navigate(['delete-product', product.productName], { relativeTo: this.activatedRoute });
  }
}

function updateFacilityBaselineEnergyConsumption(
  payload: UnderlyingAgreementVariationApplySavePayload,
  form: FacilityBaselineEnergyConsumptionFormModel,
  facilityId: string,
  products: ProductVariableEnergyConsumptionData[] | null | undefined,
): UnderlyingAgreementVariationApplySavePayload {
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

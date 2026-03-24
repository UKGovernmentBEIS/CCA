import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, startWith } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { BaselineEnergyDraftService, normaliseNumber, underlyingAgreementQuery } from '@requests/common';
import { requireProductsValidator, uniqueFieldValidator } from '@shared/validators';

import { ProductVariableEnergyConsumptionData } from 'cca-api';

export type ProductFormGroup = FormGroup<{
  productName: FormControl<ProductVariableEnergyConsumptionData['productName']>;
  baselineYear: FormControl<number>;
  baselineVariableEnergy: FormControl<ProductVariableEnergyConsumptionData['energy']>;
  baselineThroughput: FormControl<ProductVariableEnergyConsumptionData['throughput']>;
  throughputUnit: FormControl<ProductVariableEnergyConsumptionData['throughputUnit']>;
  energyIntensity: FormControl<number>;
}>;

export type AddProductFormModel = FormGroup<{
  products: FormArray<ProductFormGroup>;
}>;

export const ADD_PRODUCT_FORM = new InjectionToken<AddProductFormModel>('Baseline energy add product form');

export const AddProductFormProvider: Provider = {
  provide: ADD_PRODUCT_FORM,
  deps: [ActivatedRoute, FormBuilder, DestroyRef, RequestTaskStore, BaselineEnergyDraftService],
  useFactory: (
    activatedRoute: ActivatedRoute,
    fb: FormBuilder,
    destroyRef: DestroyRef,
    requestTaskStore: RequestTaskStore,
    draftService: BaselineEnergyDraftService,
  ) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;
    const una = requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
    const facilityIndex = una.facilities?.findIndex((facility) => facility.facilityId === facilityId);

    const baselineEnergyConsumption = requestTaskStore.select(
      underlyingAgreementQuery.selectFacilityBaselineEnergyConsumption(facilityIndex),
    )();

    // Read products from draft service
    const draft = draftService.draftSignal();
    const existingProducts = draft?.products ?? baselineEnergyConsumption?.variableEnergyConsumptionDataByProduct ?? [];

    const productControls = existingProducts.length
      ? existingProducts.map((product) =>
          createProductFormGroup(fb, destroyRef, {
            ...product,
            energyIntensity: calculateEnergyIntensity(product),
          }),
        )
      : [createProductFormGroup(fb, destroyRef)];

    return fb.group({
      products: fb.array<ProductFormGroup>(productControls, {
        validators: [requireProductsValidator(() => true), (control) => uniqueFieldValidator(control, 'productName')],
      }),
    });
  },
};

export function createProductFormGroup(
  fb: FormBuilder,
  destroyRef: DestroyRef,
  value?: Partial<ProductVariableEnergyConsumptionData & { energyIntensity?: number | null }>,
): ProductFormGroup {
  const baselineYearValue = value?.baselineYear != null ? Number(value.baselineYear) : null;

  const productGroup = fb.group({
    productName: fb.control(value?.productName ?? null, [
      GovukValidators.required('Enter a product name'),
      GovukValidators.maxLength(255, 'Enter up to 255 characters'),
    ]),
    baselineYear: fb.control(baselineYearValue, [GovukValidators.required('Select baseline year')]),
    baselineVariableEnergy: fb.control(normaliseNumber(value?.energy)?.toString(), [
      GovukValidators.required('Enter the baseline variable energy'),
      GovukValidators.maxDecimalsValidator(7),
    ]),
    baselineThroughput: fb.control(normaliseNumber(value?.throughput)?.toString(), [
      GovukValidators.required('Enter the baseline throughput'),
      GovukValidators.maxDecimalsValidator(7),
      GovukValidators.positiveNumber('Enter a number greater than zero'),
    ]),
    throughputUnit: fb.control(value?.throughputUnit ?? null, [
      GovukValidators.required('Enter the throughput unit'),
      GovukValidators.maxLength(255, 'Enter up to 255 characters'),
    ]),
    energyIntensity: fb.control({ value: value?.energyIntensity ?? null, disabled: true }),
  });

  setupEnergyIntensityCalculation(productGroup, destroyRef);

  return productGroup;
}

function calculateEnergyIntensity(product: ProductVariableEnergyConsumptionData): number | null {
  const energyValue = Number(product?.energy);
  const throughputValue = Number(product?.throughput);

  if (!throughputValue) return null;

  return Number.isFinite(energyValue / throughputValue) ? energyValue / throughputValue : null;
}

function setupEnergyIntensityCalculation(group: ProductFormGroup, destroyRef: DestroyRef): void {
  const energyControl = group.controls.baselineVariableEnergy;
  const throughputControl = group.controls.baselineThroughput;
  const intensityControl = group.controls.energyIntensity;

  combineLatest([
    energyControl.valueChanges.pipe(startWith(energyControl.value)),
    throughputControl.valueChanges.pipe(startWith(throughputControl.value)),
  ])
    .pipe(takeUntilDestroyed(destroyRef))
    .subscribe(([energy, throughput]) => {
      const energyValue = Number(energy);
      const throughputValue = Number(throughput);

      if (!throughputValue) {
        intensityControl.setValue(null, { emitEvent: false });
        return;
      }

      const intensity = energyValue / throughputValue;
      intensityControl.setValue(intensity, { emitEvent: false });
    });
}

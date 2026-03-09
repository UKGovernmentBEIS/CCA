import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
  BaselineEnergyDraftService,
  FacilityBaselineEnergyConsumptionFormModel,
  facilityBaselineEnergyProductsValidator,
  normaliseNumber,
  underlyingAgreementQuery,
} from '@requests/common';

import { FacilityBaselineEnergyConsumption } from 'cca-api';

export const FACILITY_BASELINE_ENERGY_CONSUMPTION_FORM = new InjectionToken<FacilityBaselineEnergyConsumptionFormModel>(
  'Facility baseline energy consumption form',
);

export const FacilityBaselineEnergyConsumptionFormProvider: Provider = {
  provide: FACILITY_BASELINE_ENERGY_CONSUMPTION_FORM,
  deps: [ActivatedRoute, FormBuilder, RequestTaskStore, BaselineEnergyDraftService],
  useFactory: (
    activatedRoute: ActivatedRoute,
    fb: FormBuilder,
    requestTaskStore: RequestTaskStore,
    draftService: BaselineEnergyDraftService,
  ) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;
    const una = requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
    const facilityIndex = una.facilities?.findIndex((facility) => facility.facilityId === facilityId) ?? -1;

    const storeData = requestTaskStore.select(
      underlyingAgreementQuery.selectFacilityBaselineEnergyConsumption(facilityIndex),
    )();

    // Initialize draft from store (no-op if already initialized)
    draftService.initializeFromStore(storeData);
    const draft = draftService.draftSignal();

    const productsControl = fb.control<FacilityBaselineEnergyConsumption['variableEnergyConsumptionDataByProduct']>(
      draft?.products ?? [],
      {
        updateOn: 'change',
      },
    );

    const totalFixedEnergyValue = draft?.totalFixedEnergy == null ? null : normaliseNumber(draft.totalFixedEnergy);

    const form = fb.group(
      {
        // Fields that survive navigation - read from draft
        totalFixedEnergy: fb.control(totalFixedEnergyValue, {
          validators: [
            GovukValidators.required('Enter the total fixed energy consumption'),
            GovukValidators.maxDecimalsValidator(7),
          ],
          updateOn: 'change',
        }),
        hasVariableEnergy: fb.control(draft?.hasVariableEnergy ?? null, {
          validators: [GovukValidators.required('Select yes if the facility has variable energy consumption')],
          updateOn: 'change',
        }),
        variableEnergyType: fb.control(draft?.variableEnergyType ?? null, {
          updateOn: 'change',
        }),
        // TOTALS-specific fields - read from store (don't need to survive navigation)
        baselineVariableEnergy: fb.control(normaliseNumber(storeData?.baselineVariableEnergy), {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
        totalThroughput: fb.control(normaliseNumber(storeData?.totalThroughput), {
          validators: [
            GovukValidators.maxDecimalsValidator(7),
            GovukValidators.positiveNumber('Enter a number greater than zero'),
          ],
          updateOn: 'change',
        }),
        throughputUnit: fb.control(storeData?.throughputUnit ?? null, {
          updateOn: 'change',
        }),
        products: productsControl,
      },
      {
        updateOn: 'submit',
        validators: [facilityBaselineEnergyProductsValidator()],
      },
    );

    // React to hasVariableEnergy changes
    form.controls.hasVariableEnergy.valueChanges.pipe(takeUntilDestroyed()).subscribe((hasVariable) => {
      if (hasVariable) {
        form.controls.variableEnergyType.setValidators([
          GovukValidators.required('Select how you want to capture variable energy'),
        ]);
        // Set default value to TOTALS when hasVariableEnergy becomes true
        if (!form.controls.variableEnergyType.value) {
          form.controls.variableEnergyType.setValue('TOTALS');
        }
      } else {
        form.controls.variableEnergyType.clearValidators();
        form.controls.variableEnergyType.reset();

        // When hasVariableEnergy is false, throughput fields are required
        form.controls.totalThroughput.setValidators([
          GovukValidators.required('Enter total baseline throughput'),
          GovukValidators.maxDecimalsValidator(7),
          GovukValidators.positiveNumber('Enter a number greater than zero'),
        ]);

        form.controls.throughputUnit.setValidators([GovukValidators.required('Enter throughput unit')]);
      }

      form.controls.variableEnergyType.updateValueAndValidity({ emitEvent: false });
      form.controls.totalThroughput.updateValueAndValidity();
      form.controls.throughputUnit.updateValueAndValidity();
    });

    // Sync totalFixedEnergy changes to draft service
    form.controls.totalFixedEnergy.valueChanges.pipe(takeUntilDestroyed()).subscribe((value) => {
      draftService.updateTotalFixedEnergy(value?.toString() ?? null);
    });

    // React to variableEnergyType changes
    form.controls.variableEnergyType.valueChanges.pipe(takeUntilDestroyed()).subscribe((type) => {
      if (type === 'TOTALS') {
        form.controls.baselineVariableEnergy.setValidators([
          GovukValidators.required('Enter total baseline variable energy'),
          GovukValidators.maxDecimalsValidator(7),
        ]);

        form.controls.totalThroughput.setValidators([
          GovukValidators.required('Enter total baseline throughput'),
          GovukValidators.maxDecimalsValidator(7),
          GovukValidators.positiveNumber('Enter a number greater than zero'),
        ]);

        form.controls.throughputUnit.setValidators([GovukValidators.required('Enter throughput unit')]);
      } else {
        form.controls.baselineVariableEnergy.clearValidators();
        form.controls.totalThroughput.clearValidators();
        form.controls.throughputUnit.clearValidators();
      }

      form.controls.baselineVariableEnergy.updateValueAndValidity();
      form.controls.totalThroughput.updateValueAndValidity();
      form.controls.throughputUnit.updateValueAndValidity();
    });

    return form;
  },
};

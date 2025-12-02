import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
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
  deps: [ActivatedRoute, FormBuilder, RequestTaskStore],
  useFactory: (activatedRoute: ActivatedRoute, fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;
    const una = requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
    const facilityIndex = una.facilities?.findIndex((facility) => facility.facilityId === facilityId) ?? -1;

    const baselineEnergyConsumption = requestTaskStore.select(
      underlyingAgreementQuery.selectFacilityBaselineEnergyConsumption(facilityIndex),
    )();

    const products = baselineEnergyConsumption?.variableEnergyConsumptionDataByProduct ?? [];

    const productsControl = fb.control<FacilityBaselineEnergyConsumption['variableEnergyConsumptionDataByProduct']>(
      products,
      {
        validators: [],
        updateOn: 'change',
      },
    );

    const form = fb.group(
      {
        totalFixedEnergy: fb.control(normaliseNumber(baselineEnergyConsumption?.totalFixedEnergy), {
          validators: [
            GovukValidators.required('Enter the total fixed energy consumption'),
            GovukValidators.maxDecimalsValidator(7),
          ],
          updateOn: 'change',
        }),
        hasVariableEnergy: fb.control(baselineEnergyConsumption?.hasVariableEnergy ?? null, {
          validators: [GovukValidators.required('Select yes if the facility has variable energy consumption')],
          updateOn: 'change',
        }),
        baselineVariableEnergy: fb.control(normaliseNumber(baselineEnergyConsumption?.baselineVariableEnergy), {
          validators: [GovukValidators.maxDecimalsValidator(7)],
          updateOn: 'change',
        }),
        totalThroughput: fb.control(normaliseNumber(baselineEnergyConsumption?.totalThroughput), {
          validators: [
            GovukValidators.maxDecimalsValidator(7),
            GovukValidators.positiveNumber('Enter a number greater than zero'),
          ],
          updateOn: 'change',
        }),
        throughputUnit: fb.control(baselineEnergyConsumption?.throughputUnit ?? null, {
          updateOn: 'change',
        }),
        variableEnergyType: fb.control(baselineEnergyConsumption?.variableEnergyType ?? null, {
          updateOn: 'change',
        }),
        products: productsControl,
      },
      {
        updateOn: 'submit',
        validators: [facilityBaselineEnergyProductsValidator()],
      },
    );

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

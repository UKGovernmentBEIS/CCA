import { InjectionToken, Provider } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { FUEL_MAP, FuelReference, FuelRow, FuelTypeKey, mapStandardFuelRows, tprFormQuery } from '@requests/common';
import { MEASUREMENT_TYPE_TO_UNIT_MAP } from '@shared/pipes';
import { toNumber } from '@shared/utils';
import { CCAGovukValidators } from '@shared/validators';

type FuelRowForm = FormGroup<{
  fuelKey: FormControl<string | null>;
  fuelType: FormControl<string>;
  co2ConversionFactor: FormControl<string>;
  deliveredEnergy: FormControl<string | null>;
  primaryEnergyConversionFactor: FormControl<string>;
  isCustom: FormControl<boolean>;
}>;

export type FuelForm = FormGroup<{
  fuels: FormArray<FuelRowForm>;
  atLeastSeventyPercentEnergyUsed: FormControl<boolean | null>;
  specialReportingMethodology: FormControl<string | null>;
}>;

export const ENERGY_FUEL_AMOUNT_DETAILS_FORM = new InjectionToken<FuelForm>('Energy fuel amount details form');

const DECIMALS_MESSAGE = 'Enter a number up to 7 decimal places';

const NON_STANDARD_FUEL_TYPE_VALIDATORS = [
  GovukValidators.required('Enter a name for the additional fuel type'),
  Validators.maxLength(255),
];

const NON_STANDARD_CO2_VALIDATORS = [
  GovukValidators.required('Enter the conversion factor for the additional fuel type'),
  GovukValidators.min(0, 'Conversion factors must be equal to or greater than zero'),
  CCAGovukValidators.maxDecimalsWithMessage(7, DECIMALS_MESSAGE),
];

const NON_STANDARD_DELIVERED_ENERGY_VALIDATORS = [
  GovukValidators.required('Enter the delivered energy for the additional fuel type'),
  GovukValidators.min(0.0000001, 'Non-standard fuels must have a value greater than zero'),
  CCAGovukValidators.maxDecimalsWithMessage(7, DECIMALS_MESSAGE),
];

const SPECIAL_REPORTING_METHODLOGY_VALIDATORS = [
  GovukValidators.required('Enter the electricity from CHP plant and dedicated generator'),
  GovukValidators.min(0, 'Enter a value equal to or greater than 0'),
  CCAGovukValidators.maxDecimalsWithMessage(7, DECIMALS_MESSAGE),
];

/**
 * Validates SRM consistency: if SRM has a value,
 * at least one of grid or non-grid electricity must have a value > 0.
 * Returns a validator function factory that captures the FormGroup context.
 */
function srmConsistencyValidator(formGroup: FormGroup): ValidatorFn {
  return (): ValidationErrors | null => {
    const fuelsArray = formGroup.get('fuels') as FormArray;
    const srmControl = formGroup.get('specialReportingMethodology');

    if (!fuelsArray || !srmControl) return null;

    const srmValue = toNumber(srmControl.getRawValue());
    const srmHasValue = srmValue > 0;

    // Check if at least one of grid or non-grid electricity has a value > 0
    const hasGridOrNonGridElectricity = fuelsArray.controls.some(
      (fuel) =>
        (fuel.value.fuelKey === 'GRID_ELECTRICITY' || fuel.value.fuelKey === 'NON_GRID_ELECTRICITY') &&
        toNumber(fuel.value.deliveredEnergy) > 0,
    );

    // If SRM has a value but no grid/non-grid electricity, set error
    if (srmHasValue && !hasGridOrNonGridElectricity) {
      return { srmInconsistent: 'Input inconsistent with SRM rules. Contact your regulator' };
    }

    return null;
  };
}

function uniqueCustomFuelTypeValidator(formArray: FormArray<FuelRowForm>) {
  const names = formArray.controls
    .filter((control) => control.controls.isCustom.value)
    .map((control) => (control.controls.fuelType.value ?? '').trim().toLowerCase())
    .filter(Boolean);

  const hasDuplicate = names.some((name, index) => names.indexOf(name) !== index);
  return hasDuplicate ? { duplicateNames: 'Enter a unique name for this fuel' } : null;
}

export const EnergyFuelAmountDetailsFormProvider: Provider = {
  provide: ENERGY_FUEL_AMOUNT_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const energyFuelDetails = requestTaskStore.select(tprFormQuery.selectPerformanceData)()?.energyFuelDetails;
    const referenceData = requestTaskStore.select(tprFormQuery.selectReferenceData)();
    const electricitySuppliedFromCHP = energyFuelDetails?.electricitySuppliedFromCHP;

    const measurementUnit =
      MEASUREMENT_TYPE_TO_UNIT_MAP[referenceData?.baselineAndTargets?.measurementType] ??
      MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH;

    const savedRows = mapStandardFuelRows(
      Object.entries(FUEL_MAP) as [FuelTypeKey, FuelReference][],
      energyFuelDetails?.standardFuels,
      measurementUnit,
    );

    const nonStandardFuels = energyFuelDetails?.nonStandardFuels ?? [];

    const fuelFormArray = fb.array(savedRows.map((row) => createFuelRowGroup(fb, row)));

    fuelFormArray.setValidators(() => uniqueCustomFuelTypeValidator(fuelFormArray));

    nonStandardFuels.forEach((fuel) => {
      fuelFormArray.push(
        createNonStandardFuelRowGroup(fb, {
          fuelType: fuel.name,
          co2ConversionFactor: fuel.conversionFactor,
          deliveredEnergy: fuel.deliveredEnergy,
        }),
      );
    });

    const group = fb.group({
      fuels: fuelFormArray,
      atLeastSeventyPercentEnergyUsed: fb.control<boolean | null>(
        energyFuelDetails?.atLeastSeventyPercentEnergyUsed ?? null,
        GovukValidators.required(
          'Select yes if at least 70% of the total energy used in carrying out eligible activities',
        ),
      ),
      specialReportingMethodology: fb.control<string | null>(electricitySuppliedFromCHP ?? null),
    });

    if (referenceData?.baselineAndTargets?.usedReportingMechanism) {
      group.controls.specialReportingMethodology.setValidators([
        ...SPECIAL_REPORTING_METHODLOGY_VALIDATORS,
        srmConsistencyValidator(group),
      ]);
    } else {
      group.controls.specialReportingMethodology.setValue(null);
      group.controls.specialReportingMethodology.disable();
    }

    group.controls.specialReportingMethodology.updateValueAndValidity();

    return group;
  },
};

function createFuelRowGroup(fb: FormBuilder, row: FuelRow): FuelRowForm {
  return fb.group(
    {
      fuelKey: fb.control<string | null>(row.fuelKey),
      fuelType: fb.control<string>(row.label),
      co2ConversionFactor: fb.control<string>(row.co2ConversionFactor.toString()),
      deliveredEnergy: fb.control<string | null>(row.deliveredEnergy?.toString() ?? '0', {
        validators: [
          GovukValidators.required('Enter the delivered energy'),
          GovukValidators.min(0, 'Enter a value equal to or greater than 0'),
          CCAGovukValidators.maxDecimalsWithMessage(7, DECIMALS_MESSAGE),
        ],
        updateOn: 'change',
      }),
      primaryEnergyConversionFactor: fb.control<string>(row.primaryEnergyConversionFactor.toString()),
      isCustom: fb.control<boolean>(false),
    },
    { updateOn: 'submit' },
  );
}

export function createNonStandardFuelRowGroup(
  fb: FormBuilder,
  initial?: { fuelType?: string; co2ConversionFactor?: string | number; deliveredEnergy?: string | number },
): FuelRowForm {
  return fb.group(
    {
      fuelKey: fb.control<string | null>(null),
      fuelType: fb.control(initial?.fuelType ?? ''),
      co2ConversionFactor: fb.control(String(initial?.co2ConversionFactor ?? '0')),
      deliveredEnergy: fb.control<string | null>(
        initial?.deliveredEnergy != null ? String(initial.deliveredEnergy) : null,
        { updateOn: 'change' },
      ),
      primaryEnergyConversionFactor: fb.control('1'),
      isCustom: fb.control(true),
    },
    { updateOn: 'submit' },
  );
}

export function applyNonStandardFuelRowValidators(row: FuelRowForm): void {
  if (!row.controls.isCustom.value) return;

  row.controls.fuelType.setValidators(NON_STANDARD_FUEL_TYPE_VALIDATORS);
  row.controls.co2ConversionFactor.setValidators(NON_STANDARD_CO2_VALIDATORS);
  row.controls.deliveredEnergy.setValidators(NON_STANDARD_DELIVERED_ENERGY_VALIDATORS);

  row.controls.fuelType.updateValueAndValidity({ emitEvent: false });
  row.controls.co2ConversionFactor.updateValueAndValidity({ emitEvent: false });
  row.controls.deliveredEnergy.updateValueAndValidity({ emitEvent: false });
}

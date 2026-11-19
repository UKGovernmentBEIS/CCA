import { InjectionToken, Provider } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { toBigNumber } from '@shared/utils';

import { etsReportingQuery } from '../../../ets-reporting.selectors';
import { EtsFuelRowInput } from '../../../ets-reporting.types';
import { isValidEtsDecimal } from '../../../ets-reporting.validation';
import {
  isEtsStandardFuelName,
  mapEtsStandardFuelRows,
  normaliseEtsDecimal,
} from '../../../ets-reporting-calculations';

export type FuelRowForm = FormGroup<{
  fuelKey: FormControl<string | null>;
  fuelType: FormControl<string>;
  co2ConversionFactor: FormControl<string | number>;
  deliveredEnergy: FormControl<string | number | null>;
  primaryEnergyConversionFactor: FormControl<string>;
  isCustom: FormControl<boolean>;
}>;

export type FuelForm = FormGroup<{ fuels: FormArray<FuelRowForm> }>;

export const ENERGY_FUEL_AMOUNT_DETAILS_FORM = new InjectionToken<FuelForm>('ETS energy fuel amount details form');

const NON_STANDARD_FUEL_TYPE_VALIDATORS = [
  GovukValidators.required('Enter a name for the additional fuel type'),
  GovukValidators.pattern(/\S/, 'Enter a name for the additional fuel type'),
  GovukValidators.maxLength(255, 'Enter a fuel name with 255 characters or fewer'),
];

// Numeric inputs can represent valid seven-decimal values as 1e-7; min validators handle the sign separately.
const maxDecimalsValidator: ValidatorFn = ({ value }) =>
  value == null || value === '' || isValidEtsDecimal(String(value).replace(/^-/, ''))
    ? null
    : { pattern: 'Enter a number up to 7 decimal places' };

const NON_STANDARD_CO2_VALIDATORS = [
  GovukValidators.required('Enter the conversion factor for the additional fuel type'),
  GovukValidators.min(0, 'Conversion factors must be equal to or greater than zero'),
  maxDecimalsValidator,
];

const NON_STANDARD_DELIVERED_ENERGY_VALIDATORS = [
  GovukValidators.required('Enter the delivered energy for the additional fuel type'),
  GovukValidators.min(0.0000001, 'Non-standard fuels must have a value greater than zero'),
  maxDecimalsValidator,
];

// Put cross-row errors on the relevant input so the summary links to an accessible control.
function validateFuelRows(formArray: FormArray<FuelRowForm>): ValidationErrors | null {
  const customRows = formArray.controls.filter((row) => row.controls.isCustom.value);
  const nameControls = customRows.map((row) => row.controls.fuelType);
  const toName = (control: FormControl<string>) => (control.value ?? '').trim().toLowerCase();

  nameControls.forEach((control) => {
    const name = toName(control);
    const duplicate =
      !!name &&
      (isEtsStandardFuelName(name) || nameControls.some((other) => other !== control && toName(other) === name));
    const { duplicateName: _, ...otherErrors } = control.errors ?? {};
    if (duplicate) {
      control.setErrors({ ...otherErrors, duplicateName: 'Enter a unique name for this fuel' }, { emitEvent: false });
    } else if (control.hasError('duplicateName')) {
      control.setErrors(Object.keys(otherErrors).length ? otherErrors : null, { emitEvent: false });
    }
  });

  const standardRows = formArray.controls.filter((row) => !row.controls.isCustom.value);
  const firstDelivered = standardRows[0]?.controls.deliveredEnergy;
  if (firstDelivered) {
    const { positiveStandardFuel: _, ...otherErrors } = firstDelivered.errors ?? {};
    const hasConsumption = standardRows.some((row) => toBigNumber(row.controls.deliveredEnergy.value).isGreaterThan(0));
    if (!customRows.length && !hasConsumption) {
      firstDelivered.setErrors(
        { ...otherErrors, positiveStandardFuel: 'At least one standard fuel must have a value greater than zero' },
        { emitEvent: false },
      );
    } else if (firstDelivered.hasError('positiveStandardFuel')) {
      firstDelivered.setErrors(Object.keys(otherErrors).length ? otherErrors : null, { emitEvent: false });
    }
  }

  return null;
}

export const EnergyFuelAmountDetailsFormProvider: Provider = {
  provide: ENERGY_FUEL_AMOUNT_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore): FuelForm => {
    const energyFuelDetails = store.select(etsReportingQuery.selectEnergyFuelDetails)();
    const measurementType = store.select(etsReportingQuery.selectMeasurementType)();
    const savedRows = mapEtsStandardFuelRows(measurementType, energyFuelDetails?.standardFuels);
    const fuels = fb.array(savedRows.map((row) => createFuelRowGroup(fb, row)));

    for (const fuel of energyFuelDetails?.nonStandardFuels ?? []) {
      fuels.push(
        createNonStandardFuelRowGroup(fb, {
          fuelType: fuel.name,
          co2ConversionFactor: normaliseEtsDecimal(fuel.conversionFactor),
          deliveredEnergy: normaliseEtsDecimal(fuel.deliveredEnergy),
        }),
      );
    }
    fuels.setValidators(() => validateFuelRows(fuels));
    fuels.updateValueAndValidity();

    return fb.group({ fuels });
  },
};

function createFuelRowGroup(fb: FormBuilder, row: EtsFuelRowInput): FuelRowForm {
  return fb.group(
    {
      fuelKey: fb.control<string | null>(row.fuelKey),
      fuelType: fb.control(row.fuelType),
      co2ConversionFactor: fb.control(row.co2ConversionFactor),
      deliveredEnergy: fb.control<string | number | null>(row.deliveredEnergy, {
        validators: [
          GovukValidators.required('Enter the delivered energy'),
          GovukValidators.min(0, 'Enter a value equal to or greater than 0'),
          maxDecimalsValidator,
        ],
        updateOn: 'change',
      }),
      primaryEnergyConversionFactor: fb.control(row.primaryEnergyConversionFactor),
      isCustom: fb.control(false),
    },
    { updateOn: 'submit' },
  );
}

export function createNonStandardFuelRowGroup(
  fb: FormBuilder,
  initial?: { fuelType?: string; co2ConversionFactor?: string; deliveredEnergy?: string },
): FuelRowForm {
  return fb.group(
    {
      fuelKey: fb.control<string | null>(null),
      fuelType: fb.control(initial?.fuelType ?? ''),
      co2ConversionFactor: fb.control<string | number>(initial?.co2ConversionFactor ?? '', { updateOn: 'change' }),
      deliveredEnergy: fb.control<string | number | null>(initial?.deliveredEnergy ?? null, { updateOn: 'change' }),
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

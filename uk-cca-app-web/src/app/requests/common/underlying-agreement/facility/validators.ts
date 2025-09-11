import { AbstractControl, AsyncValidatorFn, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { catchError, map, Observable, of } from 'rxjs';

import { SchemeVersions } from '@shared/types';

import { Facility, FacilityService, FacilityTargetComposition } from 'cca-api';

import { FacilityBaselineDataFormModel } from './types';

export function facilityExistenceValidator(facilityService: FacilityService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const facilityId = control.value;

    // If empty or fails SYNC validation, don't do ASYNC validation
    if (!facilityId || control.errors) return of(null);

    return facilityService.getActiveFacilityParticipatingSchemeVersions(facilityId).pipe(
      map((schemeVersions: SchemeVersions) => {
        if (schemeVersions && schemeVersions.length > 0) return null;
        return { facilityIdNotExists: 'Enter the facility ID of an existing facility' };
      }),
      catchError((error) => {
        if (error.status === 404) return of({ facilityIdNotExists: 'Enter the facility ID of an existing facility' });
        return of({ facilityIdError: 'Unable to validate facility ID. Please try again.' });
      }),
    );
  };
}

export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const date = new Date();
    return control.value && control.value > date
      ? { invalidDate: 'The exclusion date can not be a future date' }
      : null;
  };
}

export function atLeastOneActiveValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value || !Array.isArray(control.value)) {
      return { invalid: 'Your agreement must have at least one new or live facility' };
    }

    const facilities = (control.value as Array<Facility>).filter((f) => f.status !== 'EXCLUDED');
    return facilities.length > 0 ? null : { invalid: 'Your agreement must have at least one new or live facility' };
  };
}

function updateControlError(control: AbstractControl, condition: boolean, errorKey: string, errorMessage: string) {
  const errors = control.errors || {};

  if (condition) {
    errors[errorKey] = errorMessage;
    control.setErrors(errors);
  } else {
    delete errors[errorKey];
    control.setErrors(Object.keys(errors).length ? errors : null);
  }
}

export function facilityBaselineDataConditionallyRequiredFieldsValidator(
  agreementCompositionType: FacilityTargetComposition['agreementCompositionType'],
): ValidatorFn {
  return (group: FacilityBaselineDataFormModel): ValidationErrors | null => {
    if (!group || !(group instanceof FormGroup)) return null;

    const isTwelveMonths = group.controls.isTwelveMonths.value;
    if (typeof isTwelveMonths !== 'boolean') return null;

    isTwelveMonths ? handleYesSelected(group) : handleNoSelected(group);

    handleSharedValidations(group, agreementCompositionType, isTwelveMonths);

    return null;
  };
}

function handleYesSelected(group: FacilityBaselineDataFormModel): void {
  const baselineDateControl = group.controls.baselineDate;
  const explanationControl = group.controls.explanation;

  if (!baselineDateControl || !explanationControl) return;

  const baselineDate = baselineDateControl.value;
  const explanation = explanationControl.value;
  const targetDate = new Date('2022-01-01');

  updateControlError(
    baselineDateControl,
    !baselineDate,
    'requiredBaselineDate',
    'Enter the start date of the baseline.',
  );

  updateControlError(
    explanationControl,
    baselineDate && baselineDate.getTime() > targetDate.getTime() && !explanation,
    'requiredExplanation',
    'An explanation is required when a different base year is used.',
  );
}

function handleNoSelected(group: FacilityBaselineDataFormModel): void {
  const baselineDateControl = group.controls.baselineDate;
  const explanationControl = group.controls.explanation;
  const usedReportingMechanismControl = group.controls.usedReportingMechanism;

  if (!baselineDateControl || !explanationControl || !usedReportingMechanismControl) return;

  const baselineDate = baselineDateControl.value;
  const explanation = explanationControl.value;

  updateControlError(
    baselineDateControl,
    !baselineDate,
    'requiredBaselineDate',
    'Enter the date when 12 months data will be available.',
  );

  updateControlError(
    explanationControl,
    !explanation,
    'requiredExplanation',
    'An explanation is required how the target unit fits the greenfield criteria.',
  );
}

function handleSharedValidations(
  group: FacilityBaselineDataFormModel,
  agreementCompositionType: FacilityTargetComposition['agreementCompositionType'],
  isTwelveMonths: boolean,
) {
  const energyControl = group.controls.energy;
  const usedReportingMechanismControl = group.controls.usedReportingMechanism;
  const energyCarbonFactorControl = group.controls.energyCarbonFactor;
  const baselineDateControl = group.controls.baselineDate;

  const energy = energyControl.value;
  const usedReportingMechanism = usedReportingMechanismControl.value;
  const energyCarbonFactor = energyCarbonFactorControl.value;
  const baselineDate = baselineDateControl.value;

  updateControlError(
    baselineDateControl,
    baselineDate < new Date('2022-01-01'),
    'baselineDate',
    'Provide a date on or after 01/01/2022.',
  );

  updateControlError(
    energyControl,
    !energy,
    'requiredFacility',
    isTwelveMonths
      ? 'Enter the baseline kWh in the baseline period.'
      : 'Enter the baseline (energy/carbon unit) in the baseline period.',
  );

  updateControlError(
    usedReportingMechanismControl,
    agreementCompositionType !== 'NOVEM' && typeof usedReportingMechanism !== 'boolean',
    'requiredUsedReportingMechanism',
    'Select yes if throughput was adjusted using the CHP special reporting mechanism.',
  );

  updateControlError(
    energyCarbonFactorControl,
    !energyCarbonFactor,
    'requiredEnergyCarbonFactor',
    'Enter the baseline energy to carbon conversion factor in the baseline period.',
  );
}

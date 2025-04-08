import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { TargetComposition } from 'cca-api';

import { AddBaselineDataFormModel } from './add-baseline-data/add-baseline-data-form.provider';
import { TargetCompositionFormModel } from './target-composition/target-composition-form.provider';

export function measurementTypeValidator(): ValidatorFn {
  return (control: TargetCompositionFormModel['controls']['measurementType']): ValidationErrors | null => {
    if (!control || !control.parent) {
      return null;
    }

    const sectorMeasurementType = control.parent.get('sectorAssociationMeasurementType')?.getRawValue();
    const measurementType = control.value;

    if (!measurementType) return { required: 'Select the measurement units used by the target unit.' };

    const isEnergyUnit = (unit: string) => ['ENERGY_KWH', 'ENERGY_MWH', 'ENERGY_GJ'].includes(unit);
    const isCarbonUnit = (unit: string) => ['CARBON_TONNES', 'CARBON_KG'].includes(unit);

    const sectorIsEnergy = isEnergyUnit(sectorMeasurementType);
    const sectorIsCarbon = isCarbonUnit(sectorMeasurementType);
    const measurementIsEnergy = isEnergyUnit(measurementType);
    const measurementIsCarbon = isCarbonUnit(measurementType);

    if ((sectorIsEnergy && !measurementIsEnergy) || (sectorIsCarbon && !measurementIsCarbon)) {
      return {
        measurementTypeMismatch:
          'The selected measurement unit must be of the same class (energy or carbon) as used by the sector/subsector.`',
      };
    }

    return null;
  };
}

export function targetCompositionConditionallyRequiredFieldsValidator(): ValidatorFn {
  return (group: TargetCompositionFormModel): ValidationErrors | null => {
    if (!group || !(group instanceof FormGroup)) {
      return null;
    }

    const agreementCompositionTypeValue = group.get('agreementCompositionType').value;

    if (agreementCompositionTypeValue === null || agreementCompositionTypeValue === 'NOVEM') {
      return null;
    }

    const isTargetUnitThroughputMeasuredControl = group.get('isTargetUnitThroughputMeasured');
    const throughputUnitControl = group.get('throughputUnit');
    const conversionEvidencesControl = group.get('conversionEvidences');

    const isTargetUnitThroughputMeasured = isTargetUnitThroughputMeasuredControl.value;
    const throughputUnit = throughputUnitControl.value;
    const conversionEvidences = conversionEvidencesControl.value;
    const sectorAssociationThroughputUnit = group.get('sectorAssociationThroughputUnit').value;

    updateControlError(
      isTargetUnitThroughputMeasuredControl,
      typeof isTargetUnitThroughputMeasured !== 'boolean' && !!sectorAssociationThroughputUnit,
      'requiredTargetUnitThroughputMeasured',
      'Select no if the target unit uses the same measurement unit as the umbrella agreement',
    );

    updateControlError(
      throughputUnitControl,
      !isThrouputUnitValid(isTargetUnitThroughputMeasured, throughputUnit, sectorAssociationThroughputUnit),
      'requiredThroughputUnit',
      'Target unit throughput unit cannot be blank.',
    );

    updateControlError(
      conversionEvidencesControl,
      isTargetUnitThroughputMeasured && (!conversionEvidences || conversionEvidences?.length === 0),
      'requiredConversionEvidences',
      'Conversion evidences cannot be blank.',
    );

    return null;
  };
}

export function addBaselineDataConditionallyRequiredFieldsValidator(
  agreementCompositionType: TargetComposition['agreementCompositionType'],
): ValidatorFn {
  return (group: AddBaselineDataFormModel): ValidationErrors | null => {
    if (!group || !(group instanceof FormGroup)) {
      return null;
    }

    const isTwelveMonths = group.get('isTwelveMonths').value;

    if (typeof isTwelveMonths !== 'boolean') return null;

    isTwelveMonths ? handleYesSelected(group) : handleNoSelected(group);

    handleSharedValidations(group, agreementCompositionType, isTwelveMonths);

    return null;
  };
}

function handleYesSelected(group: AddBaselineDataFormModel): void {
  const baselineDateControl = group.get('baselineDate');
  const explanationControl = group.get('explanation');

  if (!baselineDateControl || !explanationControl) {
    return;
  }

  const baselineDate = baselineDateControl.value;
  const explanation = explanationControl.value;
  const targetDate = new Date('2018-01-01');

  updateControlError(
    baselineDateControl,
    !baselineDate,
    'requiredBaselineDate',
    'Enter the start date of the baseline.',
  );

  updateControlError(
    explanationControl,
    baselineDate && baselineDate.getTime() !== targetDate.getTime() && !explanation,
    'requiredExplanation',
    'An explanation is required when a different base year is used.',
  );
}

function handleNoSelected(group: AddBaselineDataFormModel): void {
  const baselineDateControl = group.get('baselineDate');
  const explanationControl = group.get('explanation');
  const usedReportingMechanismControl = group.get('usedReportingMechanism');
  const throughputControl = group.get('throughput');

  if (!baselineDateControl || !explanationControl || !usedReportingMechanismControl || !throughputControl) {
    return;
  }

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
  group: AddBaselineDataFormModel,
  agreementCompositionType: TargetComposition['agreementCompositionType'],
  isTwelveMonths: boolean,
) {
  const energyControl = group.controls.energy;
  const usedReportingMechanismControl = group.controls.usedReportingMechanism;
  const throughputControl = group.controls.throughput;
  const energyCarbonFactorControl = group.controls.energyCarbonFactor;

  const energy = energyControl.value;
  const usedReportingMechanism = usedReportingMechanismControl.value;
  const throughput = throughputControl.value;
  const energyCarbonFactor = energyCarbonFactorControl.value;

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
    throughputControl,
    agreementCompositionType !== 'NOVEM' && !throughput,
    'requiredThroughput',
    'Enter the throughput in the baseline period.',
  );

  updateControlError(
    energyCarbonFactorControl,
    !energyCarbonFactor,
    'requiredEnergyCarbonFactor',
    'Enter the baseline energy to carbon conversion factor in the baseline period.',
  );
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

const isThrouputUnitValid = (
  isTargetUnitThroughputMeasured: boolean,
  throughputUnitValue: string,
  sectorAssociationThroughputUnit: string,
) => {
  if (typeof isTargetUnitThroughputMeasured !== 'boolean' && !sectorAssociationThroughputUnit) {
    return !!throughputUnitValue;
  }

  if (isTargetUnitThroughputMeasured) return !!throughputUnitValue;
  return true;
};

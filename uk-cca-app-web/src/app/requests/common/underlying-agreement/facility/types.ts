import { FormControl, FormGroup } from '@angular/forms';

import { UuidFilePair } from '@shared/components';

import { FacilityBaselineData, FacilityBaselineEnergyConsumption } from 'cca-api';

export type FacilityBaselineDataFormModel = FormGroup<{
  isTwelveMonths: FormControl<FacilityBaselineData['isTwelveMonths']>;
  baselineDate: FormControl<Date>;
  explanation: FormControl<FacilityBaselineData['explanation']>;
  greenfieldEvidences: FormControl<UuidFilePair[]>;
  energy: FormControl<FacilityBaselineData['energy']>;
  usedReportingMechanism: FormControl<FacilityBaselineData['usedReportingMechanism']>;
  energyCarbonFactor: FormControl<FacilityBaselineData['energyCarbonFactor']>;
}>;

export type FacilityBaselineEnergyConsumptionFormModel = FormGroup<{
  totalFixedEnergy: FormControl<FacilityBaselineEnergyConsumption['totalFixedEnergy']>;
  hasVariableEnergy: FormControl<FacilityBaselineEnergyConsumption['hasVariableEnergy']>;
  baselineVariableEnergy: FormControl<FacilityBaselineEnergyConsumption['baselineVariableEnergy']>;
  totalThroughput: FormControl<FacilityBaselineEnergyConsumption['totalThroughput']>;
  throughputUnit: FormControl<FacilityBaselineEnergyConsumption['throughputUnit']>;
  variableEnergyType: FormControl<FacilityBaselineEnergyConsumption['variableEnergyType']>;
  products: FormControl<FacilityBaselineEnergyConsumption['variableEnergyConsumptionDataByProduct']>;
}>;

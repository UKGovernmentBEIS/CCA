import { FormControl, FormGroup } from '@angular/forms';

import { UuidFilePair } from '@shared/components';

import { BaselineData, FacilityTargetComposition, TargetComposition } from 'cca-api';

export type TargetCompositionFormModel = FormGroup<{
  calculatorFile: FormControl<UuidFilePair>;
  sectorAssociationMeasurementType: FormControl<string>;
  sectorAssociationThroughputUnit: FormControl<string>;
  measurementType: FormControl<TargetComposition['measurementType'] | null>;
  agreementCompositionType: FormControl<TargetComposition['agreementCompositionType']>;
  isTargetUnitThroughputMeasured: FormControl<boolean>;
  throughputUnit: FormControl<string>;
  conversionFactor: FormControl<number>;
  conversionEvidences: FormControl<UuidFilePair[]>;
}>;

export type FacilityTargetCompositionFormModel = FormGroup<{
  calculatorFile: FormControl<UuidFilePair>;
  measurementType: FormControl<FacilityTargetComposition['measurementType'] | null>;
  agreementCompositionType: FormControl<FacilityTargetComposition['agreementCompositionType']>;
}>;

export type AddBaselineDataFormModel = FormGroup<{
  energy: FormControl<BaselineData['energy']>;
  isTwelveMonths: FormControl<BaselineData['isTwelveMonths']>;
  baselineDate: FormControl<Date>;
  explanation: FormControl<BaselineData['explanation']>;
  greenfieldEvidences: FormControl<UuidFilePair[]>;
  facility: FormControl<BaselineData['energy']>;
  usedReportingMechanism: FormControl<BaselineData['usedReportingMechanism']>;
  throughput: FormControl<BaselineData['throughput']>;
  energyCarbonFactor: FormControl<BaselineData['energyCarbonFactor']>;
}>;

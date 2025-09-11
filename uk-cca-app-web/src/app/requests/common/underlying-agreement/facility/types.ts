import { FormControl, FormGroup } from '@angular/forms';

import { UuidFilePair } from '@shared/components';

import { FacilityBaselineData } from 'cca-api';

export type FacilityBaselineDataFormModel = FormGroup<{
  isTwelveMonths: FormControl<FacilityBaselineData['isTwelveMonths']>;
  baselineDate: FormControl<Date>;
  explanation: FormControl<FacilityBaselineData['explanation']>;
  greenfieldEvidences: FormControl<UuidFilePair[]>;
  energy: FormControl<FacilityBaselineData['energy']>;
  usedReportingMechanism: FormControl<FacilityBaselineData['usedReportingMechanism']>;
  energyCarbonFactor: FormControl<FacilityBaselineData['energyCarbonFactor']>;
}>;

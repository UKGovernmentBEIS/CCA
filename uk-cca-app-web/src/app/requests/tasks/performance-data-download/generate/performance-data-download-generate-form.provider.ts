import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { PerformanceDataTargetPeriodEnum } from '@requests/common';

export type GeneratePerformanceDataFormModel = FormGroup<{
  targetPeriodType: FormControl<PerformanceDataTargetPeriodEnum>;
}>;

export const GENERATE_PERFORMANCE_DATA_FORM = new InjectionToken<GeneratePerformanceDataFormModel>(
  'Generate performance data form',
);

export const PerformanceDataDownloadGenerateFormProvider: Provider = {
  provide: GENERATE_PERFORMANCE_DATA_FORM,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) => {
    return fb.group({
      targetPeriodType: fb.control(PerformanceDataTargetPeriodEnum.TP6, [
        GovukValidators.required('Please select an option'),
      ]),
    });
  },
};

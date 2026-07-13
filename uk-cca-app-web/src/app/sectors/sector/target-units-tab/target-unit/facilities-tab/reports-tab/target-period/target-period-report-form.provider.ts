import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { FacilityTargetPeriodReportStore } from '../../facility-target-period-report.store';

export type FacilityTargetPeriodReportFormModel = FormGroup<{
  targetPeriodType: FormControl<'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9'>;
  reportType: FormControl<'INTERIM' | 'FINAL'>;
}>;

export const FACILITY_TARGET_PERIOD_REPORT_FORM = new InjectionToken<FacilityTargetPeriodReportFormModel>(
  'Facility target period report form',
);

export const FacilityTargetPeriodReportFormProvider: Provider = {
  provide: FACILITY_TARGET_PERIOD_REPORT_FORM,
  deps: [FormBuilder, FacilityTargetPeriodReportStore],
  useFactory: (fb: FormBuilder, store: FacilityTargetPeriodReportStore) => {
    const state = store.state;

    return fb.group({
      targetPeriodType: fb.control(state.targetPeriodType ?? null),
      reportType: fb.control(state.reportType ?? null),
    });
  },
};

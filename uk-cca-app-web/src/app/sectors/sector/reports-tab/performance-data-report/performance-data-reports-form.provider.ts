import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { SectorAccountsPerformanceReportSearchCriteria } from 'cca-api';

export type Criteria = SectorAccountsPerformanceReportSearchCriteria;
type TargetPeriodType = Criteria['targetPeriodType'];
export type PerformanceDataReportsFormModel = FormGroup<{
  targetUnitBusinessId: FormControl<Criteria['targetUnitBusinessId']>;
  targetPeriodType: FormControl<Criteria['targetPeriodType']>;
  performanceOutcome: FormControl<Criteria['performanceOutcome']>;
  submissionType: FormControl<Criteria['submissionType']>;
}>;

export const PERFORMANCE_DATA_REPORTS_FORM = new InjectionToken<PerformanceDataReportsFormModel>(
  'Performance data reports form',
);

export const initialValues = {
  targetUnitBusinessId: null,
  targetPeriodType: 'TP6' as TargetPeriodType,
  performanceOutcome: null,
  submissionType: null,
};

export const PerformanceDataReportsFormProvider: Provider = {
  provide: PERFORMANCE_DATA_REPORTS_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    return fb.group({
      targetUnitBusinessId: fb.control(route.snapshot.queryParamMap.get('targetUnitBusinessId'), {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      targetPeriodType: fb.control(route.snapshot.queryParamMap.get('targetPeriodType')),
      performanceOutcome: fb.control(route.snapshot.queryParamMap.get('performanceOutcome')),
      submissionType: fb.control(route.snapshot.queryParamMap.get('submissionType')),
    });
  },
};

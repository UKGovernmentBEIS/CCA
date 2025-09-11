import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { SectorAccountPerformanceDataReportSearchCriteria } from 'cca-api';

type TargetPeriodType = PerformanceDataCriteria['targetPeriodType'];

export type PerformanceDataCriteria = SectorAccountPerformanceDataReportSearchCriteria;

export type PerformanceDataReportFormModel = FormGroup<{
  targetUnitAccountBusinessId: FormControl<PerformanceDataCriteria['targetUnitAccountBusinessId']>;
  targetPeriodType: FormControl<PerformanceDataCriteria['targetPeriodType']>;
  performanceOutcome: FormControl<PerformanceDataCriteria['performanceOutcome']>;
  submissionType: FormControl<PerformanceDataCriteria['submissionType']>;
}>;

export const PERFORMANCE_DATA_REPORT_FORM = new InjectionToken<PerformanceDataReportFormModel>(
  'Performance data report form',
);

export const performanceDataInitialValues = {
  targetUnitAccountBusinessId: null,
  targetPeriodType: 'TP6' as TargetPeriodType,
  performanceOutcome: null,
  submissionType: null,
  pageNumber: 1,
  pageSize: 50,
};

export const PerformanceDataReportFormProvider: Provider = {
  provide: PERFORMANCE_DATA_REPORT_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const queryParamMap = route.snapshot.queryParamMap;

    return fb.group({
      targetUnitAccountBusinessId: fb.control(queryParamMap.get('targetUnitAccountBusinessId'), {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      targetPeriodType: fb.control(queryParamMap.get('targetPeriodType') ?? 'TP6'),
      performanceOutcome: fb.control(queryParamMap.get('performanceOutcome')),
      submissionType: fb.control(queryParamMap.get('submissionType')),
    });
  },
};

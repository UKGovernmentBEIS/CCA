import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { SectorPerformanceAccountTemplateDataReportSearchCriteria } from 'cca-api';

export type PatCriteria = SectorPerformanceAccountTemplateDataReportSearchCriteria;

export type PatReportFormModel = FormGroup<{
  targetUnitAccountBusinessId: FormControl<PatCriteria['targetUnitAccountBusinessId']>;
  targetPeriodType: FormControl<PatCriteria['targetPeriodType']>;
  status: FormControl<PatCriteria['status']>;
  submissionType: FormControl<PatCriteria['submissionType']>;
}>;

export const PAT_REPORT_FORM = new InjectionToken<PatReportFormModel>('PAT report form');

export const initialPatValues = {
  targetUnitAccountBusinessId: null,
  targetPeriodType: 'TP6' as PatCriteria['targetPeriodType'],
  status: null,
  submissionType: null,
  pageNumber: 1,
  pageSize: 50,
};

export const PatReportFormProvider: Provider = {
  provide: PAT_REPORT_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const queryParamMap = route.snapshot.queryParamMap;

    return fb.group({
      targetUnitAccountBusinessId: fb.control(queryParamMap.get('targetUnitAccountBusinessId'), [
        GovukValidators.minLength(3, 'Enter at least 3 characters'),
        GovukValidators.maxLength(255, 'Enter up to 255 characters'),
      ]),
      targetPeriodType: fb.control(queryParamMap.get('targetPeriodType') ?? 'TP6'),
      status: fb.control(queryParamMap.get('status')),
      submissionType: fb.control(queryParamMap.get('submissionType')),
    });
  },
};

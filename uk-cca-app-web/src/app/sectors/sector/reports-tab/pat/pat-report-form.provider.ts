import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import {
  SectorPerformanceAccountTemplateDataReportItemDTO,
  SectorPerformanceAccountTemplateDataReportSearchCriteria,
} from 'cca-api';

export type PatCriteria = SectorPerformanceAccountTemplateDataReportSearchCriteria;
export type PatReportStatus = PatCriteria['status'];

export type PatReportItem = SectorPerformanceAccountTemplateDataReportItemDTO;

/** TP6 target unit reports; every later year reports per facility under CCA3. */
export const PAT_ACCOUNT_YEAR = 2024;
export const PAT_FACILITY_YEARS: number[] = [2026, 2027, 2028, 2029, 2030];
export const PAT_YEARS: number[] = [PAT_ACCOUNT_YEAR, ...PAT_FACILITY_YEARS];

export type PatReportFormModel = FormGroup<{
  term: FormControl<PatCriteria['term']>;
  status: FormControl<PatCriteria['status']>;
}>;

export const PAT_REPORT_FORM = new InjectionToken<PatReportFormModel>('PAT report form');

export const patInitialValues: Partial<PatCriteria> = {
  term: null,
  status: null,
};

export function toPatTargetPeriodYear(value: string | null | undefined): number | null {
  if (value == null) return null;

  const targetPeriodYear = Number(value);

  return PAT_YEARS.includes(targetPeriodYear) ? targetPeriodYear : null;
}

export function isFacilityPatYear(targetPeriodYear: number | null | undefined): boolean {
  return targetPeriodYear != null && PAT_FACILITY_YEARS.includes(targetPeriodYear);
}

export function getPatReportStatus(value: string | null | undefined): PatReportStatus | null {
  return value === 'SUBMITTED' || value === 'OUTSTANDING' ? value : null;
}

export const PatReportFormProvider: Provider = {
  provide: PAT_REPORT_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const queryParamMap = route.snapshot.queryParamMap;

    return fb.group({
      term: fb.control(queryParamMap.get('term'), {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      status: fb.control(getPatReportStatus(queryParamMap.get('status'))),
    });
  },
};

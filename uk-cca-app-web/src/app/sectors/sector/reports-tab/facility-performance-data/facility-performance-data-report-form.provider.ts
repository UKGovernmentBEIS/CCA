import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { SectorFacilityPerformanceDataReportSearchCriteria } from 'cca-api';

export type FacilityPerformanceDataCriteria = SectorFacilityPerformanceDataReportSearchCriteria;
export type FacilityTargetPeriodType = Extract<
  FacilityPerformanceDataCriteria['targetPeriodType'],
  'TP7' | 'TP8' | 'TP9'
>;
export type FacilityTargetPeriodReportType = FacilityPerformanceDataCriteria['targetPeriodReportType'];
export type FacilityReportStatus = FacilityPerformanceDataCriteria['reportStatus'];
export type FacilityReportSubType = FacilityPerformanceDataCriteria['subType'];

export type FacilityPerformanceDataReportFormModel = FormGroup<{
  facilityOrTargetUnitAccountBusinessId: FormControl<
    FacilityPerformanceDataCriteria['facilityOrTargetUnitAccountBusinessId']
  >;
  reportStatus: FormControl<FacilityPerformanceDataCriteria['reportStatus']>;
  subType: FormControl<FacilityPerformanceDataCriteria['subType']>;
}>;

export const FACILITY_PERFORMANCE_DATA_REPORT_FORM = new InjectionToken<FacilityPerformanceDataReportFormModel>(
  'Facility performance data report form',
);

export const facilityPerformanceDataInitialValues = {
  facilityOrTargetUnitAccountBusinessId: null,
  reportStatus: null,
  subType: null,
};

export function isFacilityTargetPeriod(value: string): value is FacilityTargetPeriodType {
  return value === 'TP7' || value === 'TP8' || value === 'TP9';
}

export function getDefaultTargetPeriodReportType(targetPeriodType: string): FacilityTargetPeriodReportType | null {
  return isFacilityTargetPeriod(targetPeriodType) ? 'FINAL' : null;
}

/**
 * Interim facility reports only exist for TP8 and TP9; every other facility target period resolves to the final TPR.
 */
export function getTargetPeriodReportType(
  targetPeriodType: string,
  targetPeriodReportType: string,
): FacilityTargetPeriodReportType {
  if ((targetPeriodType === 'TP8' || targetPeriodType === 'TP9') && targetPeriodReportType === 'INTERIM') {
    return 'INTERIM';
  }

  return 'FINAL';
}

export function getReportStatus(
  value: string,
  targetPeriodReportType: FacilityTargetPeriodReportType,
): FacilityReportStatus {
  if (targetPeriodReportType === 'INTERIM') {
    return value === 'SUBMITTED' || value === 'OUTSTANDING' ? value : null;
  }

  return value === 'TARGET_MET' || value === 'TARGET_NOT_MET' || value === 'OUTSTANDING' ? value : null;
}

export function getSubType(value: string): FacilityReportSubType {
  return value === 'PRIMARY' || value === 'SECONDARY' ? value : null;
}

/**
 * Subtype is a final TPR-only field. Interim reports and outstanding rows have no submitted report subtype to filter on.
 */
export function getApplicableSubType(
  value: string,
  targetPeriodReportType: FacilityTargetPeriodReportType,
  reportStatus: FacilityReportStatus,
): FacilityReportSubType {
  if (targetPeriodReportType === 'INTERIM' || reportStatus === 'OUTSTANDING') return null;

  return getSubType(value);
}

export const FacilityPerformanceDataReportFormProvider: Provider = {
  provide: FACILITY_PERFORMANCE_DATA_REPORT_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const queryParamMap = route.snapshot.queryParamMap;

    return fb.group({
      facilityOrTargetUnitAccountBusinessId: fb.control(queryParamMap.get('facilityOrTargetUnitAccountBusinessId'), {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      reportStatus: fb.control(queryParamMap.get('reportStatus')),
      subType: fb.control(queryParamMap.get('subType')),
    });
  },
};

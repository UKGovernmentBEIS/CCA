import { SectorFacilityPerformanceDataReportItemDTO } from 'cca-api';

export const facilityPerformanceReportStatusLabels = {
  TARGET_MET: 'Target met',
  TARGET_NOT_MET: 'Target not met',
  SUBMITTED: 'Submitted',
  OUTSTANDING: 'Outstanding',
} satisfies Record<NonNullable<SectorFacilityPerformanceDataReportItemDTO['reportStatus']>, string>;

export function getFacilityPerformanceReportStatusLabel(value: string | null | undefined): string {
  if (!value) return '';

  return Object.hasOwn(facilityPerformanceReportStatusLabels, value)
    ? facilityPerformanceReportStatusLabels[value as keyof typeof facilityPerformanceReportStatusLabels]
    : value;
}

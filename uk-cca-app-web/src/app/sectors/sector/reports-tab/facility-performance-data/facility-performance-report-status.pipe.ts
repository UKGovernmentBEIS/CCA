import { Pipe, PipeTransform } from '@angular/core';

export enum FacilityPerformanceReportStatusEnum {
  TARGET_MET = 'Target met',
  TARGET_NOT_MET = 'Target not met',
  SUBMITTED = 'Submitted',
  OUTSTANDING = 'Outstanding',
}

@Pipe({ name: 'facilityPerformanceReportStatus' })
export class FacilityPerformanceReportStatusPipe implements PipeTransform {
  transform(value: string | null): string {
    if (!value) return '';

    const text = FacilityPerformanceReportStatusEnum[value];

    return text ?? value;
  }
}

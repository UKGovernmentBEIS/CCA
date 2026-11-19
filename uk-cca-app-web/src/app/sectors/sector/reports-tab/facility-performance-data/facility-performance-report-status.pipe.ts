import { Pipe, PipeTransform } from '@angular/core';

import { getFacilityPerformanceReportStatusLabel } from '@shared/utils';

@Pipe({ name: 'facilityPerformanceReportStatus' })
export class FacilityPerformanceReportStatusPipe implements PipeTransform {
  transform(value: string | null): string {
    return getFacilityPerformanceReportStatusLabel(value);
  }
}

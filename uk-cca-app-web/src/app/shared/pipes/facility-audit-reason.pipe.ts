import { Pipe, PipeTransform } from '@angular/core';

import { FacilityAuditViewDTO } from 'cca-api';

export type FacilityAuditReason = FacilityAuditViewDTO['reasons'][number];

export const facilityAuditReasonMap: Record<FacilityAuditReason, string> = {
  ELIGIBILITY: 'Eligibility',
  SEVENTY_RULE_EVALUATION: '70% evaluation',
  BASE_YEAR_DATA: 'Base year data',
  REPORTING_DATA: 'Reporting data',
  NON_COMPLIANCE: 'Non-compliance',
  OTHER: 'Other',
};

export function transformFacilityAuditReason(reason: FacilityAuditReason): string {
  return reason ? facilityAuditReasonMap[reason] : '';
}

@Pipe({ name: 'facilityAuditReason' })
export class FacilityAuditReasonPipe implements PipeTransform {
  transform = transformFacilityAuditReason;
}

import { NonComplianceFacilityDTO } from 'cca-api';

import { transformWorkflowLabel } from './transform-workflow-label';

export function formatRelevantWorkflow(workflowId: string, allRelevantWorkflows: Record<string, string> = {}): string {
  const workflowLabel = allRelevantWorkflows[workflowId];

  return workflowLabel ? `${workflowId} - ${transformWorkflowLabel(workflowLabel)}` : workflowId;
}

export function formatRelevantFacility(
  facility: NonComplianceFacilityDTO,
  allRelevantFacilities: Record<string, string> = {},
): string {
  const facilityName = allRelevantFacilities[facility.facilityBusinessId];

  return facilityName ? `${facility.facilityBusinessId} - ${facilityName}` : facility.facilityBusinessId;
}

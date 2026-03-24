import { NonComplianceDetails } from 'cca-api';

export const NON_COMPLIANCE_TYPE_LABELS: Record<NonComplianceDetails['nonComplianceType'], string> = {
  FAILURE_TO_PROVIDE_TPR: 'Failure to provide target period report',
  FAILURE_TO_PROVIDE_PR: 'Failure to provide performance report',
  FAILURE_TO_PROVIDE_OTHER_INFO: 'Failure to provide other information',
  FAILURE_TO_NOTIFY_OF_AN_ERROR: 'Failure to notify of an error',
  FAILURE_TO_NOTIFY_FACILITY_NO_LONGER_ELIGIBLE: 'Failure to notify facility no longer eligible',
  FAILURE_TO_NOTIFY_OTHER_REQUIREMENT_UNA: 'Failure to notify other requirement (UNA)',
  INACCURATE_TPR_INFO: 'Inaccurate target period report information',
  INACCURATE_PR_INFO: 'Inaccurate performance report information',
  INACCURATE_OTHER_INFO: 'Inaccurate other information',
};

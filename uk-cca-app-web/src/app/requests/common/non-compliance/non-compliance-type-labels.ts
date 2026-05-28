import { NonComplianceDetails } from 'cca-api';

export const NON_COMPLIANCE_TYPE_LABELS: Record<NonComplianceDetails['nonComplianceType'], string> = {
  FAILURE_TO_PROVIDE_TPR: 'failure to provide the Target Period Report or Interim Target Period Report',
  FAILURE_TO_PROVIDE_PR: 'failure to provide the Performance Report',
  FAILURE_TO_PROVIDE_OTHER_INFO: 'failure to provide any other information requested by the administrator',
  FAILURE_TO_NOTIFY_OF_AN_ERROR: 'failure to notify us of an error in the base year data or any Report',
  FAILURE_TO_NOTIFY_FACILITY_NO_LONGER_ELIGIBLE: 'failure to notify that a facility is no longer eligible',
  FAILURE_TO_NOTIFY_OTHER_REQUIREMENT_UNA:
    'failure to notify us of any other requirement set out in the underlying agreement',
  INACCURATE_TPR_INFO: 'providing inaccurate information in a Target Period Report or Interim Target Period Report',
  INACCURATE_PR_INFO: 'providing inaccurate information in a Performance Report',
  INACCURATE_OTHER_INFO: 'providing inaccurate any other information requested by the administrator',
};

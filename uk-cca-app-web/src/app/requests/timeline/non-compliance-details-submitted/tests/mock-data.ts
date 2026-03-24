import { RequestActionState } from '@netz/common/store';

import { NonComplianceDetailsSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

export const nonComplianceDetailsSubmitted: NonComplianceDetailsSubmittedRequestActionPayload = {
  payloadType: 'NON_COMPLIANCE_DETAILS_SUBMITTED_PAYLOAD',
  nonComplianceDetails: {
    nonComplianceType: 'FAILURE_TO_PROVIDE_PR',
    nonCompliantDate: '2020-03-03',
    compliantDate: '2020-02-02',
    comment: 'sgfxcfb',
    relevantWorkflows: ['ADS_1-T00037-VAR-1', 'ADS_1-T00037-VAR-2', 'ADS_1-T00037-VAR-3'],
    relevantFacilities: [
      { facilityBusinessId: 'ADS_1-F00035', isHistorical: false },
      { facilityBusinessId: 'erwywyweyer', isHistorical: true },
    ],
    isEnforcementResponseNoticeRequired: false,
    explanation: 'fsfgs',
  },
};

export const nonComplianceDetailsSubmittedRequestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'NON_COMPLIANCE_DETAILS_SUBMITTED',
  payload: nonComplianceDetailsSubmitted,
  requestId: 'ADS_1-F00035-NON-COMP-1',
  requestType: 'NON_COMPLIANCE_DETAILS',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-07-18T11:36:29.249126Z',
};

export const nonComplianceDetailsSubmittedActionStateMock: RequestActionState = {
  action: nonComplianceDetailsSubmittedRequestActionDTO,
};

import { RequestActionState } from '@netz/common/store';

import {
  AuditCorrectiveActionResponse,
  AuditDetails,
  AuditDetailsAndCorrectiveActions,
  AuditDetailsCorrectiveActionsSubmittedRequestActionPayload,
  AuditDetermination,
  AuditReasonDetails,
  AuditTrackCorrectiveActionsRequestTaskPayload,
  CorrectiveActions,
  PreAuditReviewDetails,
  PreAuditReviewSubmittedRequestActionPayload,
  RequestActionDTO,
  RequestedDocuments,
} from 'cca-api';

// PRE_AUDIT REVIEW
export const mockAuditDeterminationCompleted: AuditDetermination = {
  reviewCompletionDate: '2025-05-05',
  furtherAuditNeeded: true,
  reviewComments: 'asdsadsadad',
};

export const mockRequestedDocumentsCompleted: RequestedDocuments = {
  auditMaterialReceivedDate: '2025-05-05',
  manufacturingProcessFile: '',
  processFlowMapsFile: 'afaad342-8870-4102-842e-f8d3bc7f1d2d',
  annotatedSitePlansFile: '',
  eligibleProcessFile: '',
  directlyAssociatedActivitiesFile: '',
  seventyPerCentRuleEvidenceFile: '',
  baseYearTargetPeriodEvidenceFiles: ['697e73a7-0056-4906-a4a8-eb48072edb73'],
  additionalDocuments: ['9170efd8-5b5b-41a9-9d72-bf28e8da85ab'],
  additionalInformation: 'asdsadsadad',
};

export const mockAuditReasonDetailsCompleted: AuditReasonDetails = {
  reasonsForAudit: ['BASE_YEAR_DATA', 'ELIGIBILITY'],
  comment: 'blah blah',
};

export const mockPreAuditReviewDetailsCompleted: PreAuditReviewDetails = {
  auditDetermination: mockAuditDeterminationCompleted,
  requestedDocuments: mockRequestedDocumentsCompleted,
  auditReasonDetails: mockAuditReasonDetailsCompleted,
};

export const mockPreAuditReviewCompletedPayload: PreAuditReviewSubmittedRequestActionPayload = {
  preAuditReviewDetails: mockPreAuditReviewDetailsCompleted,
};

export const mockPreAuditReviewCompletedRequestActionDTO: RequestActionDTO = {
  id: 100,
  type: 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED',
  payload: mockPreAuditReviewCompletedPayload,
  requestId: 'ADS_1-F00001-AUDT-1',
  requestType: 'FACILITY_AUDIT',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-11-11T12:13:33.283607Z',
};

export const mockPreAuditReviewCompletedActionState: RequestActionState = {
  action: mockPreAuditReviewCompletedRequestActionDTO,
};

// DETAILS CORRECTIVE ACTIONS
export const mockAuditDetails: AuditDetails = {
  auditTechnique: 'DESK_BASED_INTERVIEW',
  auditDate: '2025-01-01',
  comments: 'dslkjfkjdsn',
  finalAuditReportDate: '2025-01-01',
  auditDocuments: ['feecfb95-f4c4-47a1-b1b8-4acc68329eca'],
};

export const mockCorrectiveActions: CorrectiveActions = {
  hasActions: true,
  actions: [
    {
      title: '1',
      details: 'mkmknuhyn\n7',
      deadline: '2025-01-01',
    },
  ],
};

export const mockAuditDetailsAndCorrectiveActions: AuditDetailsAndCorrectiveActions = {
  auditDetails: mockAuditDetails,
  correctiveActions: mockCorrectiveActions,
};

export const mockDetailsCorrectiveActionsCompletedPayload: AuditDetailsCorrectiveActionsSubmittedRequestActionPayload =
  {
    payloadType: 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED_PAYLOAD',
    auditDetailsAndCorrectiveActions: mockAuditDetailsAndCorrectiveActions,
    facilityAuditAttachments: {
      'e3d8cda6-8382-42df-91d5-8fb804e47a11': 'sample_profile1.png',
      'feecfb95-f4c4-47a1-b1b8-4acc68329eca': 'sample_profile1.png',
    },
  };

export const mockDetailsCorrectiveActionsCompletedRequestActionDTO: RequestActionDTO = {
  id: 153,
  type: 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED',
  payload: mockDetailsCorrectiveActionsCompletedPayload,
  requestId: 'ADS_1-F00001-AUDT-1',
  requestType: 'FACILITY_AUDIT',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-11-17T10:52:54.016167Z',
};

export const mockDetailsCorrectiveActionsCompletedActionState: RequestActionState = {
  action: mockDetailsCorrectiveActionsCompletedRequestActionDTO,
};

// TRACK CORRECTIVE ACTIONS
export const mockCorrectiveActionResponses: Record<string, AuditCorrectiveActionResponse> = {
  '1': {
    title: '1',
    details: 'sd fdsf sdf',
    deadline: '2025-01-01',
    isActionCarriedOut: false,
    comments: 'dsc,mknds ,cnds kcjn dksjlcds',
    evidenceFiles: [],
  },
  '2': {
    title: '2',
    details: 'sdvsdvdsv',
    deadline: '2025-02-02',
    isActionCarriedOut: true,
    actionCarriedOutDate: '2025-01-01',
    comments: 'slvkj dsklvj sdlk vsd',
    evidenceFiles: ['ce8eb4cb-0b5a-40df-a2f1-bc5e850b53bf'],
  },
  '3': {
    title: '3',
    details: 'vsdvdsvdsvdsvsdv',
    deadline: '2025-03-03',
    isActionCarriedOut: true,
    actionCarriedOutDate: '2025-03-03',
    comments: 'sdkh dskfjh dskjfh ds[oifh sd;jkhf dskj',
    evidenceFiles: ['5178f11f-7499-4070-9f40-149e4f368f39'],
  },
};

export const mockTracksCorrectiveActionsCompletedPayload: AuditTrackCorrectiveActionsRequestTaskPayload = {
  payloadType: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED_PAYLOAD',
  auditTrackCorrectiveActions: {
    correctiveActionResponses: mockCorrectiveActionResponses,
  },
  facilityAuditAttachments: {
    '5178f11f-7499-4070-9f40-149e4f368f39': 'sample_profile1.png',
    'ce8eb4cb-0b5a-40df-a2f1-bc5e850b53bf': 'sample_profile1.png',
  },
};

export const mockTrackCorrectiveActionsCompletedRequestActionDTO: RequestActionDTO = {
  id: 162,
  type: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED',
  payload: mockTracksCorrectiveActionsCompletedPayload,
  requestId: 'ADS_1-F00006-AUDT-1',
  requestType: 'FACILITY_AUDIT',
  requestAccountId: 9,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-11-25T11:08:51.958364Z',
};

export const mockTrackCorrectiveActionsCompletedActionState: RequestActionState = {
  action: mockTrackCorrectiveActionsCompletedRequestActionDTO,
};

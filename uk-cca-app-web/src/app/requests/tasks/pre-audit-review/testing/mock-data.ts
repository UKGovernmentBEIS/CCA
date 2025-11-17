import { mockRequestTaskState } from '@requests/common';

import {
  AuditDetermination,
  AuditReasonDetails,
  PreAuditReviewDetails,
  PreAuditReviewSubmitRequestTaskPayload,
  RequestedDocuments,
} from 'cca-api';

export const mockAuditDetermination: AuditDetermination = {
  reviewCompletionDate: '2025-05-05',
  furtherAuditNeeded: true,
  reviewComments: 'asdsadsadad',
};

export const mockRequestedDocuments: RequestedDocuments = {
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

export const mockAuditReasonDetails: AuditReasonDetails = {
  reasonsForAudit: ['BASE_YEAR_DATA', 'ELIGIBILITY'],
  comment: 'blah blah',
};

export const mockPreAuditReviewDetails: PreAuditReviewDetails = {
  auditDetermination: mockAuditDetermination,
  requestedDocuments: mockRequestedDocuments,
  auditReasonDetails: mockAuditReasonDetails,
};

export const mockPreAuditReviewState = {
  ...mockRequestTaskState,
  payload: {
    preAuditReviewDetails: mockPreAuditReviewDetails,
    sectionsCompleted: {},
  } as PreAuditReviewSubmitRequestTaskPayload,
  requestTaskId: 123,
};

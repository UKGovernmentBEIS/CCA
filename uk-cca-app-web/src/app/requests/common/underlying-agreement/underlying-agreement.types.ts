import { InjectionToken } from '@angular/core';

import { UuidFilePair } from '@shared/components';

import {
  BaselineData,
  Facility,
  TargetComposition,
  TargetPeriod5Details,
  UnderlyingAgreementReviewRequestTaskPayload,
  UnderlyingAgreementSubmitRequestTaskPayload,
  UnderlyingAgreementVariationReviewRequestTaskPayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

export type UNARequestTaskPayload =
  | UNAApplicationRequestTaskPayload
  | UNAReviewRequestTaskPayload
  | UNAVariationRequestTaskPayload
  | UNAVariationReviewRequestTaskPayload;

export type UNAApplicationRequestTaskPayload = UnderlyingAgreementSubmitRequestTaskPayload;

export type UNAReviewRequestTaskPayload = UnderlyingAgreementReviewRequestTaskPayload;

export type UNAVariationRequestTaskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload;

export type UNAVariationReviewRequestTaskPayload = UnderlyingAgreementVariationReviewRequestTaskPayload;

export const UPLOAD_SECTION_ATTACHMENT_TYPE = {
  UNDERLYING_AGREEMENT_APPLICATION_SUBMIT: 'UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT',
  UNDERLYING_AGREEMENT_APPLICATION_REVIEW: 'UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT',
  UNDERLYING_AGREEMENT_VARIATION_SUBMIT: 'UNDERLYING_AGREEMENT_VARIATION_UPLOAD_SECTION_ATTACHMENT',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW: 'UNDERLYING_AGREEMENT_VARIATION_UPLOAD_SECTION_ATTACHMENT',
};

export const UPLOAD_DECISION_ATTACHMENT_TYPE = {
  UNDERLYING_AGREEMENT_APPLICATION_REVIEW: 'UNDERLYING_AGREEMENT_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW:
    'UNDERLYING_AGREEMENT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
};

export type AuthorisationAndAdditionalEvidenceUserInput = {
  authorisationAttachmentIds: UuidFilePair[];
  additionalEvidenceAttachmentIds: UuidFilePair[];
};

export type TargetPeriodExistUserInput = Pick<TargetPeriod5Details, 'exist'>;

export type TargetCompositionUserInput = Omit<TargetComposition, 'calculatorFile' | 'conversionEvidences'> & {
  calculatorFile: UuidFilePair;
  conversionEvidences: UuidFilePair[];
};

export type BaselineDataUserInput = Omit<BaselineData, 'greenfieldEvidences'> & {
  greenfieldEvidences: UuidFilePair[];
};

export const VARIATION_DETAILS_SUBTASK = 'underlyingAgreementVariationDetails';
export const REVIEW_TARGET_UNIT_DETAILS_SUBTASK = 'underlyingAgreementTargetUnitDetails';
export const MANAGE_FACILITIES_SUBTASK = 'manageFacilities';
export const FACILITIES_SUBTASK = 'facilities';
export const AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK = 'authorisationAndAdditionalEvidence';
export const PROVIDE_EVIDENCE_SUBTASK = 'underlyingAgreementActivationDetails';
export const OVERALL_DECISION_SUBTASK = 'overallDecision';

export enum BaselineAndTargetPeriodsSubtasks {
  TARGET_PERIOD_5_DETAILS = 'targetPeriod5Details',
  TARGET_PERIOD_6_DETAILS = 'targetPeriod6Details',
}

export type TargetPeriod =
  | BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS
  | BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;

export const BASELINE_AND_TARGETS_SUBTASK = new InjectionToken<TargetPeriod>('target period');

export const staticSections = [
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  MANAGE_FACILITIES_SUBTASK,
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
] as const;

export const staticGroupDecisions = [
  'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
  'TARGET_PERIOD5_DETAILS',
  'TARGET_PERIOD6_DETAILS',
  'TARGET_UNIT_DETAILS',
] as const;

export const staticVariationSections = [VARIATION_DETAILS_SUBTASK, ...staticSections];

export const staticVariationGroupDecisions = ['VARIATION_DETAILS', ...staticGroupDecisions] as const;

export enum StaticVariationGroupDecisionsEnum {
  'AUTHORISATION_AND_ADDITIONAL_EVIDENCE' = 'authorisationAndAdditionalEvidence',
  'TARGET_PERIOD5_DETAILS' = 'targetPeriod5Details',
  'TARGET_PERIOD6_DETAILS' = 'targetPeriod6Details',
  'TARGET_UNIT_DETAILS' = 'underlyingAgreementTargetUnitDetails',
}

export enum ReviewTargetUnitDetailsWizardStep {
  TARGET_UNIT_DETAILS = 'target-unit-details',
  OPERATOR_ADDRESS = 'operator-address',
  RESPONSIBLE_PERSON = 'responsible-person',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export enum ReviewTargetUnitDetailsReviewWizardStep {
  TARGET_UNIT_DETAILS = 'target-unit-details',
  OPERATOR_ADDRESS = 'operator-address',
  RESPONSIBLE_PERSON = 'responsible-person',
  DECISION = 'decision',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export enum AuthorisationAdditionalEvidenceWizardStep {
  PROVIDE_EVIDENCE = 'provide-evidence',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export enum AuthorisationAdditionalEvidenceReviewWizardStep {
  PROVIDE_EVIDENCE = 'provide-evidence',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  DECISION = 'decision',
  SUMMARY = 'summary',
}

export enum ManageFacilitiesWizardStep {
  ADD_FACILITY = 'add',
  EDIT_FACILITY = 'edit',
  DELETE_FACILITY = 'delete',
  EXCLUDE_FACILITY = 'exclude',
  UNDO_FACILITY = 'undo',
  SUMMARY = 'summary',
}

export enum FacilityWizardStep {
  DETAILS = 'details',
  CONTACT_DETAILS = 'contact-details',
  ELIGIBILITY_DETAILS = 'eligibility-details',
  EXTENT = 'extent',
  APPLY_RULE = 'apply-rule',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export enum FacilityWizardReviewStep {
  DETAILS = 'details',
  CONTACT_DETAILS = 'contact-details',
  ELIGIBILITY_DETAILS = 'eligibility-details',
  EXTENT = 'extent',
  APPLY_RULE = 'apply-rule',
  DECISION = 'decision',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export enum BaseLineAndTargetsStep {
  BASELINE_EXISTS = 'baseline-exists',
  TARGET_COMPOSITION = 'target-composition',
  ADD_BASELINE_DATA = 'add-baseline-data',
  ADD_TARGETS = 'add-targets',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export enum BaseLineAndTargetsReviewStep {
  BASELINE_EXISTS = 'baseline-exists',
  TARGET_COMPOSITION = 'target-composition',
  ADD_BASELINE_DATA = 'add-baseline-data',
  ADD_TARGETS = 'add-targets',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  DECISION = 'decision',
  SUMMARY = 'summary',
}

export enum VariationDetailsReviewWizardStep {
  DETAILS = 'details',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  DECISION = 'decision',
  SUMMARY = 'summary',
}

export enum ProvideEvidenceWizardStep {
  DETAILS = 'details',
  CHECK_ANSWERS = 'check-answers',
  SUMMARY = 'summary',
}

export enum OverallDecisionWizardStep {
  AVAILABLE_ACTIONS = 'actions',
  EXPLANATION = 'explanation',
  ADDITIONAL_INFO = 'additional-info',
  CHECK_ANSWERS = 'check-answers',
  SUMMARY = 'summary',
}

export enum VariationDetailsWizardStep {
  DETAILS = 'details',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
  SUMMARY = 'summary',
}

export type FacilityItemViewModel = {
  name?: string;
  facilityId: string;
  status?: Facility['status'];
  excludedDate?: Facility['excludedDate'];
};

export type ManageFacilities = {
  facilityItems: FacilityItemViewModel[];
};

export function toFacilityItemViewModel(facility: Facility): FacilityItemViewModel {
  return { facilityId: facility.facilityId, name: facility.facilityDetails.name, status: facility.status };
}

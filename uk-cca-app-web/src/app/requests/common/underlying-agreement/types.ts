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

import { TaskItemStatus } from '../task-item-status';

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
export const AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK = 'authorisationAndAdditionalEvidence';
export const PROVIDE_EVIDENCE_SUBTASK = 'underlyingAgreementActivationDetails';
export const OVERALL_DECISION_SUBTASK = 'overallDecision';
export const CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK = 'activationDetails';

export enum BaselineAndTargetPeriodsSubtasks {
  TARGET_PERIOD_5_DETAILS = 'targetPeriod5Details',
  TARGET_PERIOD_6_DETAILS = 'targetPeriod6Details',
}

export type TargetPeriod =
  | BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS
  | BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS;

export const BASELINE_AND_TARGETS_SUBTASK = new InjectionToken<TargetPeriod>('target period');

export const nonBaselineAndTargetsTasks = [
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
];

export const nonFacilitySections = [
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
  BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
];

export const nonFacilityReviewSections = [
  OVERALL_DECISION_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
];

export const staticReviewGroupDecisions = ['AUTHORISATION_AND_ADDITIONAL_EVIDENCE', 'TARGET_UNIT_DETAILS'] as const;

export const staticGroupDecisions = [
  'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
  'TARGET_PERIOD5_DETAILS',
  'TARGET_PERIOD6_DETAILS',
  'TARGET_UNIT_DETAILS',
] as const;

export const SUBTASK_TO_DECISION_MAP = {
  [VARIATION_DETAILS_SUBTASK]: 'VARIATION_DETAILS',
  [REVIEW_TARGET_UNIT_DETAILS_SUBTASK]: 'TARGET_UNIT_DETAILS',
  [BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS]: 'TARGET_PERIOD5_DETAILS',
  [BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS]: 'TARGET_PERIOD6_DETAILS',
  [AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK]: 'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
};

export const DECISION_TO_SUBTASK_MAP = Object.fromEntries(
  Object.entries(SUBTASK_TO_DECISION_MAP).map(([subtask, decision]) => [decision, subtask]),
);

export const staticVariationSections = [VARIATION_DETAILS_SUBTASK, ...nonFacilitySections];

export const staticVariationSectionsWithoutBaselineAndTargets = [
  VARIATION_DETAILS_SUBTASK,
  ...nonBaselineAndTargetsTasks,
];

export const staticVariationGroupDecisions = ['VARIATION_DETAILS', ...staticGroupDecisions] as const;

export enum ReviewTargetUnitDetailsWizardStep {
  COMPANY_REGISTRATION_NUMBER = 'company-registration-number',
  TARGET_UNIT_DETAILS = 'target-unit-details',
  OPERATOR_ADDRESS = 'operator-address',
  RESPONSIBLE_PERSON = 'responsible-person',
}

export enum AuthorisationAdditionalEvidenceWizardStep {
  PROVIDE_EVIDENCE = 'provide-evidence',
  CHECK_YOUR_ANSWERS = 'check-your-answers',
}

export enum ManageFacilitiesWizardStep {
  ADD_FACILITY = 'add',
  EDIT_FACILITY = 'edit',
  DELETE_FACILITY = 'delete',
  EXCLUDE_FACILITY = 'exclude',
  UNDO_FACILITY = 'undo',
}

export enum FacilityWizardStep {
  DETAILS = 'details',
  CONTACT_DETAILS = 'contact-details',
  ELIGIBILITY_DETAILS = 'eligibility-details',
  EXTENT = 'extent',
  APPLY_RULE = 'apply-rule',
  TARGET_COMPOSITION = 'target-composition',
  BASELINE_DATA = 'baseline-data',
  BASELINE_ENERGY_CONSUMPTION = 'baseline-energy-consumption',
  TARGETS = 'targets',
}

export enum BaseLineAndTargetsStep {
  BASELINE_EXISTS = 'baseline-exists',
  TARGET_COMPOSITION = 'target-composition',
  ADD_BASELINE_DATA = 'add-baseline-data',
  ADD_BASELINE_ENERGY_CONSUMPTION = 'add-baseline-energy-consumption',
  ADD_TARGETS = 'add-targets',
}

export enum BaseLineAndTargetsReviewStep {
  BASELINE_EXISTS = 'baseline-exists',
  TARGET_COMPOSITION = 'target-composition',
  ADD_BASELINE_DATA = 'add-baseline-data',
  ADD_TARGETS = 'add-targets',
}

export enum OverallDecisionWizardStep {
  AVAILABLE_ACTIONS = 'actions',
  EXPLANATION = 'explanation',
  ADDITIONAL_INFO = 'additional-info',
}

export type FacilityItemViewModel = {
  name?: string;
  facilityId: string;
  status?: Facility['status'];
  excludedDate?: Facility['excludedDate'];
  workflowStatus?: TaskItemStatus | null;
};

export type FacilityTimelineItemViewModel = {
  name?: string;
  facilityId: string;
  status?: Facility['status'];
  decisionStatus?: TaskItemStatus | null;
};

export function toFacilityItemViewModel(facility: Facility): FacilityItemViewModel {
  return { facilityId: facility.facilityId, name: facility.facilityDetails.name, status: facility.status };
}

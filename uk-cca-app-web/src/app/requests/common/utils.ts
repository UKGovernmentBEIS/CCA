import { TaskItem, TaskSection } from '@netz/common/model';
import { SchemeVersion, SchemeVersions } from '@shared/types';
import { produce } from 'immer';

import {
  AccountReferenceData,
  CompanyProfileDTO,
  Facility,
  TargetUnitAccountContactDTO,
  UnderlyingAgreementReviewRequestTaskPayload,
  UnderlyingAgreementTargetUnitDetails,
  UnderlyingAgreementTargetUnitResponsiblePerson,
  UnderlyingAgreementVariationApplySaveTargetUnitDetails,
} from 'cca-api';

import { TaskItemStatus } from './task-item-status';
import { OVERALL_DECISION_SUBTASK, staticVariationSections } from './underlying-agreement';

export type TUDetailsSection = {
  isCompanyRegistrationNumber: boolean;
  operatorName: string;
  operatorType: 'LIMITED_COMPANY' | 'PARTNERSHIP' | 'SOLE_TRADER' | 'NONE';
  companyRegistrationNumber: string;
  registrationNumberMissingReason: string;
  subsectorAssociationId: number;
  subsectorAssociationName: string;
};

export function sortFacilitiesById(facilities: Facility[]): Facility[] {
  if (!facilities || facilities.length === 0) return [];
  return produce(facilities, (draft) => {
    draft.sort((a, b) => a.facilityId.localeCompare(b.facilityId, 'en-GB', { numeric: true, sensitivity: 'base' }));
  });
}

/**
 * Determines wheather the subtask links are clickable depending on the isEditable argument
 */
export function filterEditableTaskLinks(sections: TaskSection[], isEditable: boolean): TaskSection[] {
  if (isEditable) return sections;
  return sections.map((s) => ({
    ...s,
    tasks: s.tasks.map(filterTaskLinks),
  }));
}

const filterTaskLinks = (task: TaskItem): TaskItem =>
  task.status === TaskItemStatus.NOT_STARTED ? { ...task, link: '' } : task;

const deletePhoneNumberAndJobTitle = (
  responsiblePerson: TargetUnitAccountContactDTO,
): UnderlyingAgreementTargetUnitResponsiblePerson =>
  produce(responsiblePerson, (rp) => {
    delete rp.phoneNumber;
    delete rp.jobTitle;
  });

export const boolToString = (bool: boolean): 'Yes' | 'No' | null =>
  typeof bool !== 'boolean' ? null : bool ? 'Yes' : 'No';

export function overallDecisionStatus(payload: UnderlyingAgreementReviewRequestTaskPayload): TaskItemStatus {
  const status = payload.reviewSectionsCompleted[OVERALL_DECISION_SUBTASK] as TaskItemStatus;
  return status || TaskItemStatus.UNDECIDED;
}

export const transformAccountReferenceData = (
  accountReferenceData: AccountReferenceData,
): UnderlyingAgreementTargetUnitDetails => ({
  operatorName: accountReferenceData.targetUnitAccountDetails.operatorName,
  operatorAddress: accountReferenceData.targetUnitAccountDetails.address,
  responsiblePersonDetails: deletePhoneNumberAndJobTitle(
    accountReferenceData.targetUnitAccountDetails.responsiblePerson,
  ),
  operatorType: accountReferenceData.targetUnitAccountDetails.operatorType,
  isCompanyRegistrationNumber: !!accountReferenceData.targetUnitAccountDetails.companyRegistrationNumber,
  companyRegistrationNumber: accountReferenceData.targetUnitAccountDetails.companyRegistrationNumber,
  registrationNumberMissingReason: accountReferenceData.targetUnitAccountDetails.registrationNumberMissingReason,
  subsectorAssociationId: accountReferenceData.targetUnitAccountDetails.subsectorAssociationId,
  subsectorAssociationName: accountReferenceData.sectorAssociationDetails.subsectorAssociationName,
});

export function transformAccountReferenceDataToTUDetails(
  accountReferenceData: AccountReferenceData,
): UnderlyingAgreementVariationApplySaveTargetUnitDetails {
  const originalResponsiblePerson: UnderlyingAgreementTargetUnitResponsiblePerson = {
    firstName: accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.firstName,
    lastName: accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.lastName,
    email: accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.email,
    address: accountReferenceData?.targetUnitAccountDetails?.responsiblePerson?.address,
  };

  const originalTUDetails: UnderlyingAgreementVariationApplySaveTargetUnitDetails = {
    operatorName: accountReferenceData?.targetUnitAccountDetails?.operatorName,
    operatorAddress: accountReferenceData?.targetUnitAccountDetails?.address,
    responsiblePersonDetails: originalResponsiblePerson,
  };

  return originalTUDetails;
}

export const isCCA2Scheme = (schemeVersions: SchemeVersions): boolean => schemeVersions?.includes(SchemeVersion.CCA_2);

export const isCCA3Scheme = (schemeVersions: SchemeVersions): boolean => schemeVersions?.includes(SchemeVersion.CCA_3);

export const hasBothCCASchemes = (schemeVersions: SchemeVersions): boolean =>
  schemeVersions?.includes(SchemeVersion.CCA_2) && schemeVersions?.includes(SchemeVersion.CCA_3);

export const isCreationDateAfterCutOffDate = (creationDate: string, cutOffDate: string): boolean =>
  new Date(creationDate).getTime() >= new Date(cutOffDate).getTime();

export function calcManageFacilitiesStatus(
  reviewSectionsCompleted: Record<string, string>,
  facilities: Facility[],
): TaskItemStatus {
  const facilitySections = Object.keys(reviewSectionsCompleted).filter(
    (s) => ![...staticVariationSections, OVERALL_DECISION_SUBTASK].includes(s),
  );
  if (facilitySections.length === 0) return TaskItemStatus.UNDECIDED;

  const undecidedFacilityExists = facilitySections.some(
    (s) => reviewSectionsCompleted?.[s] === TaskItemStatus.UNDECIDED,
  );
  if (undecidedFacilityExists || facilities.length !== facilitySections.length) return TaskItemStatus.UNDECIDED;

  const allFacilitiesAccepted = facilitySections.every((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.ACCEPTED);
  if (allFacilitiesAccepted) return TaskItemStatus.ACCEPTED;

  const allFacilitiesRejected = facilitySections.every((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.REJECTED);
  if (allFacilitiesRejected) return TaskItemStatus.REJECTED;

  const allFacilitiesUnchanged = facilitySections.every(
    (s) => reviewSectionsCompleted?.[s] === TaskItemStatus.UNCHANGED,
  );
  if (allFacilitiesUnchanged) return TaskItemStatus.UNCHANGED;

  return facilitySections.some((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.ACCEPTED)
    ? TaskItemStatus.ACCEPTED
    : TaskItemStatus.REJECTED;
}

export function filterFieldsWithFalsyValues(obj: unknown): unknown {
  if (obj === null || obj === undefined || typeof obj !== 'object') return obj;

  return Object.keys(obj).reduce((acc, key) => {
    const value = obj[key];

    if (value) {
      acc[key] = typeof value === 'object' ? filterFieldsWithFalsyValues(value) : value;
    }

    return acc;
  }, {});
}

export function sameCompanyRegistrationNumbers(
  companyProfile: CompanyProfileDTO,
  existingCompanyRegistrationNumber: string,
): boolean {
  return companyProfile?.registrationNumber === existingCompanyRegistrationNumber;
}

export function areEntitiesIdentical(current: unknown, original: unknown): boolean {
  // Check for strict equality first (handles primitives and same reference)
  if (current === original) return true;

  // Check if either is null or not an object
  if (current == null || original == null) return false;
  if (typeof current !== 'object' || typeof original !== 'object') return false;

  // Get all keys from both objects
  const keysCurrent = Object.keys(current);
  const keysOriginal = Object.keys(original);

  // Check if they have the same number of keys
  if (keysCurrent.length !== keysOriginal.length) return false;

  // Check each key-value pair recursively
  for (const key of keysCurrent) {
    // Check if key exists in second object
    if (!(key in original)) return false;

    // Recursively check if values are identical
    if (!areEntitiesIdentical(current[key], original[key])) return false;
  }

  return true;
}

export function isStatusFinal(section: string): boolean {
  return section === TaskItemStatus.COMPLETED || section === TaskItemStatus.UNCHANGED;
}

export function resetFacilityNonComparisonFields(facility: Facility): Facility {
  return produce(facility, (f) => {
    f.excludedDate = null;
    f.status = null;
  });
}

export function extractBaselineYear(dateStr: string) {
  const date = new Date(dateStr);
  const year = date.getFullYear();

  if (Number.isNaN(year)) throw new Error('Year is not a number');

  // Determine if the year is a leap year
  const isLeapYear = year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0);

  // July is month index 6 in JS Date (0-based)
  const month = date.getMonth();
  const day = date.getDate();

  // Cutoff day: on/after 3 July in non-leap years; on/after 2 July in leap years
  const cutoffDay = isLeapYear ? 2 : 3;

  const onOrAfterCutoff = month > 6 || (month === 6 && day >= cutoffDay);

  return onOrAfterCutoff ? year + 1 : year;
}

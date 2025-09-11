import { TaskItem, TaskSection } from '@netz/common/model';
import { SchemeVersion, SchemeVersions } from '@shared/types';
import { produce } from 'immer';

import {
  AccountReferenceData,
  Facility,
  TargetUnitAccountContactDTO,
  UnderlyingAgreementReviewRequestTaskPayload,
  UnderlyingAgreementTargetUnitDetails,
  UnderlyingAgreementTargetUnitResponsiblePerson,
} from 'cca-api';

import { TaskItemStatus } from './task-item-status';
import { nonFacilityReviewSections, OVERALL_DECISION_SUBTASK } from './underlying-agreement';

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

export const transform = (accountReferenceData: AccountReferenceData): UnderlyingAgreementTargetUnitDetails => ({
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

export const isCCA2Scheme = (schemeVersions: SchemeVersions): boolean => schemeVersions?.includes(SchemeVersion.CCA_2);

export const isCCA3Scheme = (schemeVersions: SchemeVersions): boolean => schemeVersions?.includes(SchemeVersion.CCA_3);

export const hasBothCCASchemes = (schemeVersions: SchemeVersions): boolean =>
  schemeVersions?.includes(SchemeVersion.CCA_2) && schemeVersions?.includes(SchemeVersion.CCA_3);

export const isCreationDateAfterCutOffDate = (creationDate: string, cutOffDate: string): boolean =>
  new Date(creationDate).getTime() >= new Date(cutOffDate).getTime();

export function calcManageFacilitiesStatus(reviewSectionsCompleted: Record<string, string>): TaskItemStatus {
  const facilitySections = Object.keys(reviewSectionsCompleted).filter((s) => !nonFacilityReviewSections.includes(s));
  if (facilitySections.length === 0) return TaskItemStatus.UNDECIDED;

  const allFacilitiesAccepted = facilitySections.every((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.ACCEPTED);
  if (allFacilitiesAccepted) return TaskItemStatus.ACCEPTED;

  const allFacilitiesRejected = facilitySections.every((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.REJECTED);
  if (allFacilitiesRejected) return TaskItemStatus.REJECTED;

  const undecidedFacilityExists = facilitySections.some(
    (s) => reviewSectionsCompleted?.[s] === TaskItemStatus.UNDECIDED,
  );

  if (undecidedFacilityExists) return TaskItemStatus.UNDECIDED;

  return facilitySections.some((s) => reviewSectionsCompleted?.[s] === TaskItemStatus.ACCEPTED)
    ? TaskItemStatus.ACCEPTED
    : TaskItemStatus.REJECTED;
}

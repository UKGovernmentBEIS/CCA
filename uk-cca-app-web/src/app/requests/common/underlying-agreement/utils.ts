import { TaskItem, TaskSection } from '@netz/common/model';
import { produce } from 'immer';

import {
  TargetUnitAccountContactDTO,
  UnderlyingAgreementReviewRequestTaskPayload,
  UnderlyingAgreementTargetUnitResponsiblePerson,
} from 'cca-api';
import { AccountReferenceData, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { TaskItemStatus } from '../task-item-status';
import { OVERALL_DECISION_SUBTASK } from './underlying-agreement.types';

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

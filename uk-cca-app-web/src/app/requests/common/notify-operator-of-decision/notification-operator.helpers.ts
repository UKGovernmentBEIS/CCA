import { SectorUserRoleCode, transformNoticeRecipientsType } from '@shared/pipes';

import { CcaDecisionNotification, DefaultNoticeRecipient, RequestActionUserInfo } from 'cca-api';

import { NotifyOperatorOfDecisionFormModel } from './notify-operator-of-decision-form.provider';

export function toCcaDecisionNotification(
  notifyPayload: NotifyOperatorOfDecisionFormModel['value'],
): CcaDecisionNotification {
  const operators = notifyPayload.additionalUsersNotified.filter((u) => u.type === 'OPERATOR');
  const sectorUsers = notifyPayload.additionalUsersNotified.filter((u) => u.type === 'SECTOR_USER');

  return {
    operators: operators.map((u) => u.userId),
    externalContacts: notifyPayload.externalContactsNotified,
    signatory: notifyPayload.signatory,
    sectorUsers: sectorUsers.map((u) => u.userId),
  };
}

export function toNonComplianceDecisionNotification(
  notifyPayload: NotifyOperatorOfDecisionFormModel['value'],
): CcaDecisionNotification {
  const operators = notifyPayload.additionalUsersNotified.filter((u) => u.type === 'OPERATOR');

  return {
    operators: operators.map((u) => u.userId),
    externalContacts: notifyPayload.externalContactsNotified,
    signatory: notifyPayload.signatory,
  };
}

export function extractSignatoryUserFromUsersInfo(
  usersInfo: Record<string, RequestActionUserInfo>,
  signatory: CcaDecisionNotification['signatory'],
): string {
  const signatoryUserId = Object.keys(usersInfo).find((key) => key === signatory);
  if (!signatoryUserId) throw new Error('No user found from signatory id');
  return usersInfo[signatoryUserId].name;
}

export function extractSectorUsersFromUsersInfo(
  usersInfo: Record<string, RequestActionUserInfo>,
  sectorUsers: CcaDecisionNotification['sectorUsers'],
): string[] {
  const users = sectorUsers?.map((su) => usersInfo[su]) ?? [];
  return users.map((u) => `${u.name}, ${SectorUserRoleCode[u.roleCode]}`);
}

export function extractOperatorUsersFromUsersInfo(
  usersInfo: Record<string, RequestActionUserInfo>,
  operatorUsers: CcaDecisionNotification['operators'],
): string[] {
  const users = operatorUsers?.map((su) => usersInfo[su]) ?? [];
  return users.map((u) => `${u.name}, Operator user`);
}

export function transformUserContacts(defaultContacts: DefaultNoticeRecipient[]): string[] {
  return defaultContacts.map((c) => `${c.name}, ${transformNoticeRecipientsType(c.recipientType)}, ${c.email}`);
}

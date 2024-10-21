import { SectorUserRoleCode } from '@shared/pipes';

import { CcaDecisionNotification, DefaultNoticeRecipient, RequestActionUserInfo } from 'cca-api';

import { transformNoticeRecipientsType } from '../pipes';
import { NotifyOperatorOfDecisionFormModel } from './notify-operator-of-decision-form.provider';

export function toDecisionNotification(
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

export function extractSignatoryUserFromUsersInfo(
  usersInfo: { [key: string]: RequestActionUserInfo },
  signatory: CcaDecisionNotification['signatory'],
): string {
  const signatoryUserId = Object.keys(usersInfo).find((key) => key === signatory);
  if (!signatoryUserId) throw new Error('No user found from signatory id');
  return usersInfo[signatoryUserId].name;
}

export function extractSectorUsersFromUsersInfo(
  usersInfo: { [key: string]: RequestActionUserInfo },
  sectorUsers: CcaDecisionNotification['sectorUsers'],
): string[] {
  const users = sectorUsers?.map((su) => usersInfo[su]) ?? [];
  return users.map((u) => `${u.name}, ${SectorUserRoleCode[u.roleCode]}`);
}

export function extractOperatorUsersFromUsersInfo(
  usersInfo: { [key: string]: RequestActionUserInfo },
  operatorUsers: CcaDecisionNotification['operators'],
): string[] {
  const users = operatorUsers?.map((su) => usersInfo[su]) ?? [];
  return users.map((u) => `${u.name}, Operator user`);
}

export function transformUserContacts(defaultContacts: DefaultNoticeRecipient[]): string[] {
  return defaultContacts.map((c) => `${c.name}, ${transformNoticeRecipientsType(c.recipientType)}, ${c.email}`);
}

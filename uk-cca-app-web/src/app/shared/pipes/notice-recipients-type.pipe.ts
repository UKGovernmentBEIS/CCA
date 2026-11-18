import { Pipe, PipeTransform } from '@angular/core';

import { DefaultNoticeRecipient } from 'cca-api';

export const noticeRecipientsTypeMap: Record<DefaultNoticeRecipient['recipientType'], string> = {
  RESPONSIBLE_PERSON: 'Responsible person',
  ADMINISTRATIVE_CONTACT: 'Administrative contact',
  SECTOR_CONTACT: 'Sector contact',
  SECTOR_USER: 'Sector user',
  SECTOR_CONSULTANT: 'Sector consultant',
  OPERATOR: 'Operator',
};

export function transformNoticeRecipientsType(type: DefaultNoticeRecipient['recipientType'] | string): string {
  return type ? noticeRecipientsTypeMap[type] : '';
}

@Pipe({ name: 'noticeRecipientsType' })
export class NoticeRecipientsTypePipe implements PipeTransform {
  transform = transformNoticeRecipientsType;
}

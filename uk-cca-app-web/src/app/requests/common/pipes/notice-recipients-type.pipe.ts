import { Pipe, PipeTransform } from '@angular/core';

import { DefaultNoticeRecipient } from 'cca-api';

export const noticeRecipientsTypeMap: Record<DefaultNoticeRecipient['recipientType'], string> = {
  RESPONSIBLE_PERSON: 'Responsible person',
  ADMINISTRATIVE_CONTACT: 'Administrative contact',
  SECTOR_CONTACT: 'Sector contact',
  OPERATOR: 'Operator',
  SECTOR_USER: 'Sector user',
};

export function transformNoticeRecipientsType(type: DefaultNoticeRecipient['recipientType'] | string): string {
  return type ? noticeRecipientsTypeMap[type] : '';
}

@Pipe({
  name: 'noticeRecipientsType',
  standalone: true,
})
export class NoticeRecipientsTypePipe implements PipeTransform {
  transform = transformNoticeRecipientsType;
}

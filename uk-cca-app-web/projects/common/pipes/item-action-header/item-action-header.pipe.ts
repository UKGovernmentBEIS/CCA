import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionDTO, RequestActionInfoDTO } from 'cca-api';

import { ItemActionEnum } from '../item-action-type';

export function getItemActionHeader(item: RequestActionDTO | RequestActionInfoDTO) {
  switch (item.type) {
    case 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED':
    case 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED':
    case 'UNDERLYING_AGREEMENT_APPLICATION_REJECTED':
    case 'UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED':
    case 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED':
    case 'ADMIN_TERMINATION_APPLICATION_SUBMITTED':
    case 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED':
    case 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED':
      return `${ItemActionEnum[item.type]} by ${item.submitter}`;

    default:
      return ItemActionEnum[item.type] || 'Approved Application';
  }
}

@Pipe({ name: 'itemActionHeader', standalone: true, pure: true })
export class ItemActionHeaderPipe implements PipeTransform {
  transform(item: RequestActionDTO | RequestActionInfoDTO): string {
    return getItemActionHeader(item);
  }
}

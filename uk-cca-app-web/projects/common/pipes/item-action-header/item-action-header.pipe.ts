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
    case 'UNDERLYING_AGREEMENT_APPLICATION_CANCELLED':
    case 'ADMIN_TERMINATION_APPLICATION_SUBMITTED':
    case 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED':
    case 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED':
    case 'ADMIN_TERMINATION_APPLICATION_CANCELLED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED':
    case 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED':
    case 'SUBSISTENCE_FEES_RUN_SUBMITTED':
    case 'SECTOR_MOA_GENERATED':
      return `${ItemActionEnum[item.type]} by ${item.submitter}`;

    case 'UNDERLYING_AGREEMENT_APPLICATION_MIGRATED':
      return 'Underlying agreement application migrated';

    case 'SUBSISTENCE_FEES_RUN_COMPLETED':
      return 'Subsistence fees payment request run completed';
    case 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES':
      return 'Subsistence fees payment request run completed with failures';
    case 'TARGET_UNIT_MOA_GENERATED':
      return 'Subsistence fees payment request received';

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

import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'cca-api';

export enum ItemActionEnum {
  TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED = 'Target unit account submitted',
  UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED = 'Underlying agreement application submitted',
  UNDERLYING_AGREEMENT_APPLICATION_REJECTED = 'Underlying agreement application rejected',
  UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED = 'Underlying agreement application accepted',
  UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED = 'Underlying agreement application activated',
  ADMIN_TERMINATION_APPLICATION_SUBMITTED = 'Admin termination submitted',
  ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED = 'Admin termination withdrawn submitted',
  ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED = 'Admin termination final decision submitted',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED = 'Underlying agreement variation application submitted',
}

@Pipe({ name: 'itemActionType', standalone: true, pure: true })
export class ItemActionTypePipe implements PipeTransform {
  transform(type: RequestActionInfoDTO['type']): string {
    return ItemActionEnum[type] || 'Approved Application';
  }
}

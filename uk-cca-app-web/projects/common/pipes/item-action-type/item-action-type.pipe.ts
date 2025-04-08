import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'cca-api';

export enum ItemActionEnum {
  TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED = 'Target unit account submitted',
  UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED = 'Underlying agreement application submitted',
  UNDERLYING_AGREEMENT_APPLICATION_REJECTED = 'Underlying agreement application rejected',
  UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED = 'Underlying agreement application accepted',
  UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED = 'Underlying agreement application activated',
  UNDERLYING_AGREEMENT_APPLICATION_CANCELLED = 'Underlying agreement application cancelled',
  UNDERLYING_AGREEMENT_APPLICATION_MIGRATED = 'Underlying agreement application migrated',
  ADMIN_TERMINATION_APPLICATION_SUBMITTED = 'Admin termination submitted',
  ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED = 'Admin termination withdrawn submitted',
  ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED = 'Admin termination final decision submitted',
  ADMIN_TERMINATION_APPLICATION_CANCELLED = 'Admin termination cancelled',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED = 'Underlying agreement variation application submitted',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED = 'Underlying agreement variation application rejected',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED = 'Underlying agreement variation application accepted',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED = 'Underlying agreement variation activated',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED = 'Underlying agreement variation application cancelled',
  PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED = 'Performance report submitted',
  SUBSISTENCE_FEES_RUN_SUBMITTED = 'Subsistence fees payment request run submitted',
  SUBSISTENCE_FEES_RUN_COMPLETED = 'Subsistence fees payment request run completed',
  SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES = 'Subsistence fees payment request run completed with failures',
  SECTOR_MOA_GENERATED = 'Sector MoA generated',
  TARGET_UNIT_MOA_GENERATED = 'Subsistence fees payment request received',
}

@Pipe({ name: 'itemActionType', standalone: true, pure: true })
export class ItemActionTypePipe implements PipeTransform {
  transform(type: RequestActionInfoDTO['type']): string {
    return ItemActionEnum[type] || 'Approved Application';
  }
}

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
    case 'PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED':
    case 'SUBSISTENCE_FEES_RUN_SUBMITTED':
    case 'BUY_OUT_SURPLUS_RUN_SUBMITTED':
    case 'SECTOR_MOA_GENERATED':
    case 'ADMIN_TERMINATION_PEER_REVIEW_REQUESTED':
    case 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_REQUESTED':
    case 'ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
    case 'ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_REJECTED':
    case 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_ACCEPTED':
    case 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_REJECTED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
    case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED':
    case 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCELLED':
    case 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED':
    case 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED':
    case 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED':
    case 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED':
    case 'FACILITY_AUDIT_CANCELLED':
      return `${ItemActionEnum[item.type]} by ${item.submitter}`;

    case 'UNDERLYING_AGREEMENT_APPLICATION_MIGRATED':
      return 'Underlying agreement application migrated';

    case 'SUBSISTENCE_FEES_RUN_COMPLETED':
      return 'Subsistence fees payment request run completed';
    case 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES':
      return 'Subsistence fees payment request run completed with failures';
    case 'TARGET_UNIT_MOA_GENERATED':
      return 'Subsistence fees payment request received';

    case 'BUY_OUT_SURPLUS_RUN_COMPLETED':
      return 'Buy-out and surplus batch run completed';
    case 'BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES':
      return 'Buy-out and surplus batch run completed with failures';
    case 'TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED':
      return 'Buy-out fee calculated';
    case 'TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED':
      return 'Surplus calculated';

    case 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED':
      return 'CCA3 migration completed by System user';

    case 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED':
      return 'CCA2 agreement extended by System user';

    default:
      return ItemActionEnum[item.type] || 'Approved Application';
  }
}

@Pipe({ name: 'itemActionHeader', pure: true })
export class ItemActionHeaderPipe implements PipeTransform {
  transform(item: RequestActionDTO | RequestActionInfoDTO): string {
    return getItemActionHeader(item);
  }
}

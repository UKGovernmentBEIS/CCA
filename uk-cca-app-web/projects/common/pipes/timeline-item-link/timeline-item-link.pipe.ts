import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'cca-api';

@Pipe({ name: 'timelineItemLink', pure: true, standalone: true })
export class TimelineItemLinkPipe implements PipeTransform {
  transform(value: RequestActionInfoDTO, isWorkflow?: boolean): any[] {
    const routerLooks = isWorkflow ? './' : '/';

    switch (value.type) {
      case 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_REJECTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_MIGRATED':
      case 'ADMIN_TERMINATION_APPLICATION_SUBMITTED':
      case 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED':
      case 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED':
      case 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED':
      case 'SUBSISTENCE_FEES_RUN_COMPLETED':
      case 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES':
      case 'SECTOR_MOA_GENERATED':
      case 'TARGET_UNIT_MOA_GENERATED':
        return [routerLooks + 'timeline', value.id];

      case 'UNDERLYING_AGREEMENT_APPLICATION_CANCELLED':
      case 'ADMIN_TERMINATION_APPLICATION_CANCELLED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED':
      case 'SUBSISTENCE_FEES_RUN_SUBMITTED':
        return null;

      case 'PAYMENT_MARKED_AS_PAID':
        return [routerLooks + 'payment', 'actions', value.id, 'paid'];

      case 'PAYMENT_CANCELLED':
        return [routerLooks + 'payment', 'actions', value.id, 'cancelled'];

      case 'PAYMENT_MARKED_AS_RECEIVED':
        return [routerLooks + 'payment', 'actions', value.id, 'received'];

      case 'PAYMENT_COMPLETED':
        return [routerLooks + 'payment', 'actions', value.id, 'completed'];

      case 'RDE_ACCEPTED':
      case 'RDE_CANCELLED':
      case 'RDE_EXPIRED':
        return null;

      case 'RDE_FORCE_ACCEPTED':
      case 'RDE_FORCE_REJECTED':
        return [routerLooks + 'rde', 'action', value.id, 'rde-manual-approval-submitted'];

      case 'RDE_REJECTED':
        return [routerLooks + 'rde', 'action', value.id, 'rde-response-submitted'];

      case 'RDE_SUBMITTED':
        return [routerLooks + 'rde', 'action', value.id, 'rde-submitted'];

      case 'RFI_CANCELLED':
      case 'RFI_EXPIRED':
        return null;

      case 'RFI_RESPONSE_SUBMITTED':
        return [routerLooks + 'rfi', 'action', value.id, 'rfi-response-submitted'];

      case 'RFI_SUBMITTED':
        return [routerLooks + 'rfi', 'action', value.id, 'rfi-submitted'];

      case 'REQUEST_TERMINATED':
      case 'VERIFICATION_STATEMENT_CANCELLED':
        return null;

      default:
        throw new Error('Provide an action url');
    }
  }
}

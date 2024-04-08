import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'cca-api';

@Pipe({ name: 'itemActionType', standalone: true, pure: true })
export class ItemActionTypePipe implements PipeTransform {
  transform(type: RequestActionInfoDTO['type']): string {
    switch (type) {
      case 'PAYMENT_MARKED_AS_PAID':
        return 'Payment marked as paid (BACS)';
      case 'PAYMENT_CANCELLED':
        return 'Payment task cancelled';
      case 'PAYMENT_MARKED_AS_RECEIVED':
        return 'Payment marked as received';
      case 'PAYMENT_COMPLETED':
        return 'Payment confirmed via GOV.UK pay';

      case 'RDE_ACCEPTED':
        return 'Deadline extension date accepted';
      case 'RDE_CANCELLED':
      case 'RDE_REJECTED':
      case 'RDE_FORCE_REJECTED':
        return 'Deadline extension date rejected';
      case 'RDE_EXPIRED':
        return 'Deadline extension date expired';
      case 'RDE_FORCE_ACCEPTED':
        return 'Deadline extension date approved';
      case 'RDE_SUBMITTED':
        return 'Deadline extension date requested';

      case 'RFI_CANCELLED':
        return 'Request for information withdrawn';
      case 'RFI_EXPIRED':
        return 'Request for information expired';
      case 'RFI_RESPONSE_SUBMITTED':
        return 'Request for information responded';
      case 'RFI_SUBMITTED':
        return 'Request for information sent';
      case 'REQUEST_TERMINATED':
        return 'Workflow terminated by the system';

      default:
        return 'Approved Application';
    }
  }
}

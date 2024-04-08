import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionDTO, RequestActionInfoDTO } from 'cca-api';

import { ItemActionTypePipe } from './item-action-type.pipe';

@Pipe({ name: 'itemActionHeader', standalone: true, pure: true })
export class ItemActionHeaderPipe implements PipeTransform {
  transform(item: RequestActionDTO | RequestActionInfoDTO): string {
    const itemActionTypePipe = new ItemActionTypePipe();

    switch (item.type) {
      case 'PAYMENT_MARKED_AS_PAID':
        return `Payment marked as paid by ${item.submitter} (BACS)`;
      case 'PAYMENT_CANCELLED':
      case 'PAYMENT_MARKED_AS_RECEIVED':
        return `${itemActionTypePipe.transform(item.type)} by ${item.submitter}`;

      case 'RDE_ACCEPTED':
      case 'RDE_CANCELLED':
      case 'RDE_REJECTED':
      case 'RDE_FORCE_REJECTED':
      case 'RDE_FORCE_ACCEPTED':
      case 'RDE_SUBMITTED':
        return `${itemActionTypePipe.transform(item.type)} by ${item.submitter}`;

      case 'RFI_CANCELLED':
      case 'RFI_RESPONSE_SUBMITTED':
      case 'RFI_SUBMITTED':
        return `${itemActionTypePipe.transform(item.type)} by ${item.submitter}`;

      default:
        return itemActionTypePipe.transform(item.type);
    }
  }
}

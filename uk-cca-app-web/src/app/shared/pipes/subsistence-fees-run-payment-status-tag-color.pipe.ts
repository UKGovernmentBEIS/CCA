import { Pipe, PipeTransform } from '@angular/core';

import { TagColor } from '@netz/govuk-components';

@Pipe({
  name: 'subsistenceFeesRunPaymentStatusTagColor',
  standalone: true,
})
export class SubsistenceFeesRunPaymentStatusTagColorPipe implements PipeTransform {
  transform(status: string): TagColor {
    if (!status) {
      throw new Error(`invalid subsistence fees run status. Received: ${status}`);
    }

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'awaiting_payment':
        return 'yellow';

      case 'paid':
        return 'green';

      case 'overpaid':
        return 'red';

      case 'cancelled':
        return 'grey';

      default:
        throw new Error(`invalid subsistence fees run status. Received: ${status}`);
    }
  }
}

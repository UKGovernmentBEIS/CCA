import { Pipe, PipeTransform } from '@angular/core';

import { TagColor } from '@netz/govuk-components';

@Pipe({
  name: 'paymentRequestStatusTagColor',
  standalone: true,
})
export class PaymentRequestStatusTagColorPipe implements PipeTransform {
  transform(status: string): TagColor {
    if (!status) {
      throw new Error(`invalid request status. Received: ${status}`);
    }

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'in_progress':
        return 'blue';

      case 'completed':
        return 'green';

      case 'completed_with_failures':
        return 'red';

      default:
        throw new Error(`invalid request status. Received: ${status}`);
    }
  }
}

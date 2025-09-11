import { Pipe, type PipeTransform } from '@angular/core';

import type { TagColor } from '@netz/govuk-components';

@Pipe({
  name: 'statusColor',
  standalone: true,
})
export class StatusColorPipe implements PipeTransform {
  transform(status: string, cancelledColor: 'grey' | 'red' = 'red'): TagColor {
    if (!status) throw new Error(`invalid status. Received: ${status}`);

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'in_progress':
      case 'awaiting_refund':
        return 'blue';

      case 'completed':
      case 'migrated':
      case 'paid':
      case 'approved':
      case 'live':
        return 'green';

      case 'completed_with_failures':
      case 'terminated':
      case 'rejected':
      case 'withdrawn':
      case 'overpaid':
        return 'red';

      case 'refunded':
        return 'turquoise';

      case 'awaiting_payment':
        return 'yellow';

      case 'under_appeal':
        return 'orange';

      case 'not_required':
      case 'closed':
      case 'new':
        return 'grey';

      case 'cancelled':
        return cancelledColor;

      default:
        throw new Error(`invalid status. Received: ${status}`);
    }
  }
}

import { Pipe, type PipeTransform } from '@angular/core';

import type { TagColor } from '@netz/govuk-components';

@Pipe({ name: 'statusColor' })
export class StatusColorPipe implements PipeTransform {
  transform(status: string): TagColor {
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
      case 'accepted':
        return 'green';

      case 'completed_with_failures':
      case 'terminated':
      case 'rejected':
      case 'withdrawn':
      case 'overpaid':
      case 'cancelled':
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

      default:
        throw new Error(`invalid status. Received: ${status}`);
    }
  }
}

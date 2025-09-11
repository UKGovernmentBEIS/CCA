import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'status',
  standalone: true,
})
export class StatusPipe implements PipeTransform {
  transform(status?: string): string {
    if (!status) {
      throw new Error(`invalid status. Received: ${status}`);
    }

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'in_progress':
      case 'in-progress':
        return 'In progress';

      case 'completed':
        return 'Completed';

      case 'completed_with_failures':
        return 'Completed with failures';

      case 'cancelled':
        return 'Cancelled';

      case 'awaiting_payment':
        return 'Awaiting payment';

      case 'awaiting_refund':
        return 'Awaiting refund';

      case 'paid':
        return 'Paid';

      case 'overpaid':
        return 'Overpaid';

      case 'approved':
        return 'Approved';

      case 'rejected':
        return 'Rejected';

      case 'accepted':
        return 'Accepted';

      case 'undecided':
        return 'Undecided';

      case 'withdrawn':
        return 'Withdrawn';

      case 'migrated':
        return 'Migrated';

      case 'refunded':
        return 'Refunded';

      case 'not_required':
        return 'Not required';

      case 'under_appeal':
        return 'Under appeal';

      case 'terminated':
        return 'Terminated';

      case 'live':
        return 'Live';

      case 'new':
        return 'New';

      case 'excluded':
        return 'Excluded';

      case 'inactive':
        return 'Inactive';

      case 'non_financially_independent':
        return 'Non-financially independent';

      case 'financially_independent':
        return 'Financially independent';

      case 'certified':
        return 'Certified';

      case 'decertified':
        return 'Decertified';

      case 'not_yet_defined':
        return 'Not yet defined';

      default:
        throw new Error(`invalid status. Received: ${status}`);
    }
  }
}

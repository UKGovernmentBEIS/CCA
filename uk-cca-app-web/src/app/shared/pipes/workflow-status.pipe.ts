import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  standalone: true,
  name: 'workflowStatus',
})
export class WorkflowStatusPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'APPROVED':
        return 'Approved';

      case 'CANCELLED':
        return 'Cancelled';

      case 'COMPLETED':
        return 'Completed';

      case 'IN_PROGRESS':
        return 'In progress';

      case 'REJECTED':
        return 'Rejected';

      case 'WITHDRAWN':
        return 'Withdrawn';

      case 'MIGRATED':
        return 'Migrated';

      default:
        throw new Error(`invalid workflow status. received ${value}`);
    }
  }
}

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'subsistenceFeesRunMarkFacilitiesStatus',
  standalone: true,
})
export class SubsistenceFeesRunMarkFacilitiesStatusPipe implements PipeTransform {
  transform(status?: string): string {
    if (!status) {
      throw new Error(`invalid subsistence fees run status. Received: ${status}`);
    }

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'in_progress':
        return 'In progress';

      case 'completed':
        return 'Completed';

      case 'cancelled':
        return 'Cancelled';

      default:
        throw new Error(`invalid subsistence fees run status. Received: ${status}`);
    }
  }
}

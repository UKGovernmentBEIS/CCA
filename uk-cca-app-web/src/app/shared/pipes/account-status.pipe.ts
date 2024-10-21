import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'accountStatus',
  standalone: true,
})
export class AccountStatusPipe implements PipeTransform {
  transform(status?: string): string {
    if (!status) {
      throw new Error(`invalid status. received ${status}`);
    }

    switch (status) {
      case 'REJECTED':
        return 'Rejected';

      case 'LIVE':
        return 'Live';

      case 'NEW':
        return 'New';

      case 'TERMINATED':
        return 'Terminated';

      case 'CANCELLED':
        return 'Cancelled';

      default:
        throw new Error(`invalid status. received ${status}`);
    }
  }
}

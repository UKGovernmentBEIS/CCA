import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'paymentRequestProcessStatus',
  standalone: true,
})
export class PaymentRequestProcessStatusPipe implements PipeTransform {
  transform(status?: string): string {
    if (!status) {
      throw new Error(`invalid status. Received: ${status}`);
    }

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'in_progress':
        return 'In progress';

      case 'completed':
        return 'Completed';

      case 'completed_with_failures':
        return 'Completed with failures';

      default:
        throw new Error(`invalid status. Received: ${status}`);
    }
  }
}

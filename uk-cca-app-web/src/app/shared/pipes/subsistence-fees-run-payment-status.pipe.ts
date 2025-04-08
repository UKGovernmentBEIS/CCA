import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'subsistenceFeesRunPaymentStatus',
  standalone: true,
})
export class SubsistenceFeesRunPaymentStatusPipe implements PipeTransform {
  transform(status?: string): string {
    if (!status) {
      throw new Error(`invalid subsistence fees run payment status. Received: ${status}`);
    }

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'awaiting_payment':
        return 'Awaiting payment';

      case 'paid':
        return 'Paid';

      case 'overpaid':
        return 'Overpaid';

      case 'cancelled':
        return 'Cancelled';

      default:
        throw new Error(`invalid subsistence fees run payment status. Received: ${status}`);
    }
  }
}

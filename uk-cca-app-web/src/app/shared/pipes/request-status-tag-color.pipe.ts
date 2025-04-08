import { Pipe, PipeTransform } from '@angular/core';

import { TagColor } from '@netz/govuk-components';

@Pipe({ name: 'requestStatusTagColor', standalone: true })
export class RequestStatusTagColorPipe implements PipeTransform {
  transform(status: string): TagColor {
    if (!status) {
      throw new Error(`invalid request status. received ${status}`);
    }

    const lowerCaseStatus = status.toLowerCase();

    switch (lowerCaseStatus) {
      case 'approved':
        return 'green';

      case 'cancelled':
      case 'closed':
        return 'grey';

      case 'completed':
      case 'migrated':
        return 'green';

      case 'in_progress':
        return 'blue';

      case 'rejected':
      case 'withdrawn':
        return 'red';

      default:
        throw new Error(`invalid request status. received ${status}`);
    }
  }
}

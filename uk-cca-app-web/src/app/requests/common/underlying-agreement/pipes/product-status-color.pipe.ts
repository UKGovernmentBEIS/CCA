import { Pipe, type PipeTransform } from '@angular/core';

import type { TagColor } from '@netz/govuk-components';

@Pipe({ name: 'productStatusColor' })
export class ProductStatusColorPipe implements PipeTransform {
  transform(status: string | null | undefined): TagColor {
    switch ((status ?? '').toUpperCase()) {
      case 'NEW':
        return 'blue';
      case 'LIVE':
        return 'green';
      case 'EXCLUDED':
        return 'purple';
      default:
        return 'grey';
    }
  }
}

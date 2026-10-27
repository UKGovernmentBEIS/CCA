import { Pipe, PipeTransform } from '@angular/core';

import { formatGbp } from '../utils/format-gbp';

@Pipe({ name: 'formatGbp' })
export class FormatGbpPipe implements PipeTransform {
  transform(value: string | number | null | undefined): string {
    return formatGbp(value);
  }
}

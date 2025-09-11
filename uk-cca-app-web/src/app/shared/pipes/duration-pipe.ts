import { Pipe, PipeTransform } from '@angular/core';

import { GovukDatePipe } from '@netz/common/pipes';

@Pipe({
  name: 'duration',
  standalone: true,
  pure: true,
})
export class DurationPipe implements PipeTransform {
  private readonly govukDate = new GovukDatePipe();

  transform(startDate: string, endDate: string): string {
    if (!startDate || !endDate) return '';

    const formattedStart = this.govukDate.transform(startDate, 'date');
    const formattedEnd = this.govukDate.transform(endDate, 'date');

    return `${formattedStart} to ${formattedEnd}`;
  }
}

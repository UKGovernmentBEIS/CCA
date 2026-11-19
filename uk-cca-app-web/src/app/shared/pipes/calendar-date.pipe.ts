import { Pipe, PipeTransform } from '@angular/core';

import { formatCalendarDate } from '../utils/date';

@Pipe({ name: 'calendarDate' })
export class CalendarDatePipe implements PipeTransform {
  transform(value: string | Date | null | undefined, format?: string): string | null {
    return formatCalendarDate(value, format);
  }
}

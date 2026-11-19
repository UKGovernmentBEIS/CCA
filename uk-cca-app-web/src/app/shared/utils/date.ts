import { formatDate } from '@angular/common';

/** Today's local calendar date, stored at UTC midnight like DateInputComponent values. */
export function getToday(): Date {
  const now = new Date();
  return new Date(Date.UTC(now.getFullYear(), now.getMonth(), now.getDate()));
}

/**
 * Format calendar dates without shifting their UTC day to the browser's timezone.
 * Expects `yyyy-MM-dd` strings, UTC ISO timestamps or Dates at UTC midnight; not local Dates or offset-less date-times.
 */
export function formatCalendarDate(value: string | Date | null | undefined, format = 'd MMM yyyy'): string | null {
  if (!value) return null;

  // Angular parses date-only strings as local dates, so normalize them before applying UTC formatting.
  return formatDate(value instanceof Date ? value : new Date(value), format, 'en-GB', 'UTC');
}

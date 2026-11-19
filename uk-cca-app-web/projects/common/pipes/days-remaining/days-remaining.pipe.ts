import { Pipe, type PipeTransform } from '@angular/core';

@Pipe({ name: 'daysRemaining' })
export class DaysRemainingPipe implements PipeTransform {
  transform(days?: number): string {
    return days !== undefined && days !== null ? (days >= 0 ? days.toString() : 'Overdue') : '';
  }

  calcRemainingDays(providedDate: string): number {
    const now = new Date();
    const targetDate = new Date(providedDate);
    // Deadlines are calendar dates. Use UTC for arithmetic so local clock changes do not change the day count.
    const today = Date.UTC(now.getFullYear(), now.getMonth(), now.getDate());
    const deadline = Date.UTC(targetDate.getUTCFullYear(), targetDate.getUTCMonth(), targetDate.getUTCDate());

    return (deadline - today) / (1000 * 3600 * 24);
  }
}

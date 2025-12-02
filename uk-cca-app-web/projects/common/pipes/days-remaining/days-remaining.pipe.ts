import { Pipe, type PipeTransform } from '@angular/core';

@Pipe({ name: 'daysRemaining' })
export class DaysRemainingPipe implements PipeTransform {
  transform(days?: number): string {
    return days !== undefined && days !== null ? (days >= 0 ? days.toString() : 'Overdue') : '';
  }

  calcRemainingDays(providedDate: string): number {
    const today = new Date();
    const targetDate = new Date(providedDate);
    const timeDifference = targetDate.getTime() - today.getTime();
    const daysLeft = timeDifference / (1000 * 3600 * 24); // Convert the time difference from milliseconds to days
    return Math.ceil(daysLeft); // Use Math.ceil to round up if the result isn't an integer
  }
}

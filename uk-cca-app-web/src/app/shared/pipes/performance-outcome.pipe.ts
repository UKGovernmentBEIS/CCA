import { Pipe, PipeTransform } from '@angular/core';

export enum PerformanceOutcomeEnum {
  TARGET_MET = 'Target met',
  BUY_OUT_REQUIRED = 'Buy-out required',
  SURPLUS_USED_BUY_OUT_REQUIRED = 'Surplus used buy-out required',
  SURPLUS_USED = 'Surplus used',
  OUTSTANDING = 'Outstanding',
}

@Pipe({
  name: 'performanceOutcome',
  standalone: true,
})
export class PerformanceOutcomePipe implements PipeTransform {
  transform(value: string | null): string {
    const text = PerformanceOutcomeEnum[value];
    if (!text) throw new Error('invalid performance outcome');
    return text;
  }
}

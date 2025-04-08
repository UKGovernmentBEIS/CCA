import { Pipe, PipeTransform } from '@angular/core';

import { TargetPeriodPerformanceResult } from 'cca-api';

export enum TargetPeriodOutcomeEnum {
  TARGET_MET = 'Target met',
  BUY_OUT_REQUIRED = 'Buy-out required',
  SURPLUS_USED_BUY_OUT_REQUIRED = 'Surplus used buy-out required',
  SURPLUS_USED = 'Surplus used',
}

@Pipe({
  name: `targetPeriodOutcomePipe`,
  standalone: true,
})
export class TargetPeriodOutcomePipe implements PipeTransform {
  transform(value: TargetPeriodPerformanceResult['tpOutcome']): string {
    const text = TargetPeriodOutcomeEnum[value];
    if (!text) throw new Error('invalid target period outcome!');
    return text;
  }
}

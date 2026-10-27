import { Pipe, PipeTransform } from '@angular/core';

import BigNumber from 'bignumber.js';

@Pipe({ name: 'surplusAmount' })
export class SurplusAmountPipe implements PipeTransform {
  transform(value: string | null | undefined): string | null {
    return value == null ? null : new BigNumber(value).toFormat();
  }
}

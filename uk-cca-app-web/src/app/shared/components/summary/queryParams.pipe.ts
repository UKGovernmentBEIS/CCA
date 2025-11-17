import { Pipe, PipeTransform } from '@angular/core';

import { SummarySection } from './type';

@Pipe({ name: 'parseQueryParams' })
export class SummaryQueryParamsPipe implements PipeTransform {
  transform(value: SummarySection) {
    if (value.appendChangeParam === false) return {};
    return { change: true };
  }
}

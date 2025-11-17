import { Directive, HostBinding } from '@angular/core';

@Directive({ selector: 'dd[govukSummaryListColumnValue]' })
export class SummaryListColumnValueDirective {
  @HostBinding('class') className = 'govuk-summary-list__value';
}

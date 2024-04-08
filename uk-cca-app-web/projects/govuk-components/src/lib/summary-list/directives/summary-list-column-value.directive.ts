import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListColumnValue]',
  standalone: true,
})
export class SummaryListColumnValueDirective {
  @HostBinding('class') className = 'govuk-summary-list__value';
}

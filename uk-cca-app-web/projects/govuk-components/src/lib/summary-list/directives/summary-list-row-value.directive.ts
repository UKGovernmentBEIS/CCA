import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListRowValue]',
  standalone: true,
})
export class SummaryListRowValueDirective {
  @HostBinding('class') className = 'govuk-summary-list__value';
}

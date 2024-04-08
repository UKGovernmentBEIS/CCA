import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'div[govukSummaryListColumn]',
  standalone: true,
})
export class SummaryListColumnDirective {
  @HostBinding('class') className = 'govuk-summary-list__column';
}

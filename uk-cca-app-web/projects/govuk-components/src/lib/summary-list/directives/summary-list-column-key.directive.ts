import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dt[govukSummaryListColumnKey]',
  standalone: true,
})
export class SummaryListColumnKeyDirective {
  @HostBinding('class') className = 'govuk-summary-list__key';
}

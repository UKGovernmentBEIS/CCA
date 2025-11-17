import { Directive, HostBinding } from '@angular/core';

@Directive({ selector: 'dd[govukSummaryListColumnActions]' })
export class SummaryListColumnActionsDirective {
  @HostBinding('class') className = 'govuk-summary-list__actions';
}

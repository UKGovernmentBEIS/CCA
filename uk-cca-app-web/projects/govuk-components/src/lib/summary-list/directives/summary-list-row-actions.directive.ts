import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: 'dd[govukSummaryListRowActions]',
  standalone: true,
})
export class SummaryListRowActionsDirective {
  @HostBinding('class') className = 'govuk-summary-list__actions';
}

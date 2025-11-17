import { Directive, HostBinding } from '@angular/core';

@Directive({ selector: '[govukAccordionItemSummary]' })
export class AccordionItemSummaryDirective {
  @HostBinding('class')
  elementClass = 'govuk-accordion__section-summary-focus';
}

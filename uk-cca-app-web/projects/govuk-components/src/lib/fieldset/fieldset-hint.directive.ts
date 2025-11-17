import { Directive, HostBinding } from '@angular/core';

@Directive({ selector: 'div[govukFieldsetHint]' })
export class FieldsetHintDirective {
  @HostBinding('class.govuk-hint') readonly hintClass = true;
}

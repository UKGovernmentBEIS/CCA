import { Directive, HostBinding } from '@angular/core';

@Directive({ selector: 'div[govukFieldsetHint]', standalone: true })
export class FieldsetHintDirective {
  @HostBinding('class.govuk-hint') readonly hintClass = true;
}

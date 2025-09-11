import { AfterContentInit, Directive, ElementRef, HostBinding, input, contentChild } from '@angular/core';

import { FieldsetHintDirective } from './fieldset-hint.directive';

@Directive({ selector: 'fieldset[govukFieldset]', standalone: true })
export class FieldsetDirective implements AfterContentInit {
  readonly id = input('fieldset');
  readonly hint = contentChild(FieldsetHintDirective, { read: ElementRef });

  @HostBinding('class.govuk-fieldset') readonly fieldsetClass = true;

  @HostBinding('attr.id') get identifier() {
    return this.id();
  }

  @HostBinding('attr.aria-describedby') get ariaDescribedby() {
    return this.hint() ? `${this.id()}-hint` : null;
  }

  ngAfterContentInit(): void {
    const hint = this.hint();
    if (hint) {
      hint.nativeElement.id = `${this.identifier}-hint`;
    }
  }
}

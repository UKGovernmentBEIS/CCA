import {
  AfterContentInit,
  booleanAttribute,
  Directive,
  ElementRef,
  HostBinding,
  input,
  contentChild,
} from '@angular/core';

import { describedBy } from '../form/described-by';
import { FieldsetHintDirective } from './fieldset-hint.directive';

@Directive({ selector: 'fieldset[govukFieldset]' })
export class FieldsetDirective implements AfterContentInit {
  readonly id = input('fieldset');
  readonly hasError = input(false, { transform: booleanAttribute });
  readonly hint = contentChild(FieldsetHintDirective, { read: ElementRef });

  @HostBinding('class.govuk-fieldset') readonly fieldsetClass = true;

  @HostBinding('attr.id') get identifier() {
    return this.id();
  }

  @HostBinding('attr.aria-describedby') get ariaDescribedby() {
    return describedBy(this.hint() ? `${this.id()}-hint` : null, this.hasError() ? `${this.id()}-error` : null);
  }

  ngAfterContentInit(): void {
    const hint = this.hint();
    if (hint) {
      hint.nativeElement.id = `${this.identifier}-hint`;
    }
  }
}

import { Directive, ElementRef, HostBinding, HostListener, inject } from '@angular/core';

@Directive({
  selector:
    'a[govukButton], a[govukSecondaryButton], a[govukWarnButton], a[govukInverseButton], button[govukButton], button[govukWarnButton], button[govukSecondaryButton], button[govukInverseButton]',
})
export class ButtonDirective {
  private readonly elementRef = inject(ElementRef);

  @HostBinding('attr.aria-disabled')
  @HostBinding('class.govuk-button--disabled')
  get ariaDisabled(): boolean | null {
    return ButtonDirective.isButton(this.nativeElement) && this.nativeElement.disabled ? true : null;
  }

  @HostBinding('class.govuk-button')
  readonly elementClass = true;

  @HostBinding('class.govuk-button--secondary')
  get secondaryButton(): boolean {
    return this.nativeElement.hasAttribute('govuksecondarybutton');
  }

  @HostBinding('class.govuk-button--warning')
  get warningButton(): boolean {
    return this.nativeElement.hasAttribute('govukwarnbutton');
  }

  @HostBinding('class.govuk-button--inverse')
  get inverseButton(): boolean {
    return this.nativeElement.hasAttribute('govukinversebutton');
  }

  private get nativeElement(): HTMLButtonElement | HTMLAnchorElement {
    return this.elementRef.nativeElement;
  }

  private static isButton(nativeElement: HTMLButtonElement | HTMLAnchorElement): nativeElement is HTMLButtonElement {
    return nativeElement.tagName === 'BUTTON';
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(event: Event): void {
    if (event instanceof KeyboardEvent && event.code === 'Space') {
      event.target.dispatchEvent(new MouseEvent('click'));
    }
  }
}

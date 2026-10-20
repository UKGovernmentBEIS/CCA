import { AsyncPipe } from '@angular/common';
import {
  AfterContentInit,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  contentChild,
  ElementRef,
  inject,
  input,
  OnInit,
  viewChild,
} from '@angular/core';
import { ControlContainer, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Observable, startWith, tap } from 'rxjs';

import { ConditionalContentDirective, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';

import { existingControlContainer } from '../../providers/control-container.factory';

@Component({
  selector: 'cca-boolean-radio-group',
  template: `
    <div
      [formControlName]="controlName()"
      [hint]="hint()"
      [isInline]="true"
      [legend]="legend()"
      legendSize="medium"
      govuk-radio
    >
      <govuk-radio-option [value]="true" [label]="yesLabel()" />
      <govuk-radio-option [value]="false" [label]="noLabel()" />
    </div>

    <div [class.govuk-radios__conditional--hidden]="(value$ | async) !== true" [id]="conditionalId">
      <ng-content select="[govukConditionalContent]" />
    </div>
  `,
  imports: [RadioComponent, FormsModule, ReactiveFormsModule, RadioOptionComponent, AsyncPipe],
  providers: [existingControlContainer],
  viewProviders: [existingControlContainer],
})
export class BooleanRadioGroupComponent implements AfterContentInit, AfterViewInit, OnInit {
  private readonly controlContainer = inject(ControlContainer);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  protected readonly controlName = input<string>(undefined);
  protected readonly legend = input<string>(undefined);
  protected readonly hint = input<string>(undefined);
  protected readonly isEditable = input(true);

  protected readonly yesLabel = input('Yes');
  protected readonly noLabel = input('No');

  protected readonly radio = viewChild(RadioComponent, { read: ElementRef });
  value$: Observable<boolean>;

  private yesRadio: HTMLInputElement;
  private readonly conditional = contentChild(ConditionalContentDirective);

  get conditionalId() {
    return `${this.yesRadio?.id}-conditional`;
  }

  private get control() {
    return this.controlContainer.control.get(this.controlName());
  }

  ngOnInit(): void {
    this.value$ = this.control.valueChanges.pipe(
      startWith(this.control.value),
      tap((value) => this.onChoose(value)),
    );
  }

  ngAfterContentInit() {
    this.onChoose(this.control.value);
  }

  ngAfterViewInit(): void {
    this.yesRadio = this.radio().nativeElement.querySelector('input');
    this.yesRadio.setAttribute('aria-controls', this.conditionalId);
    this.setAriaExpanded(this.control.value);

    // Trigger a change detection to update the conditionalId
    this.changeDetectorRef.detectChanges();
  }

  private onChoose(value: boolean): void {
    this.setAriaExpanded(value);

    const conditional = this.conditional();
    if (conditional) {
      if (value && this.isEditable()) {
        conditional.enableControls();
      } else {
        conditional.disableControls();
      }
    }
  }

  private setAriaExpanded(value: boolean): void {
    if (this.yesRadio) {
      this.yesRadio.setAttribute('aria-expanded', value ? 'true' : 'false');
    }
  }
}

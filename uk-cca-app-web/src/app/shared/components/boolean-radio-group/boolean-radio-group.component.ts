import { AsyncPipe } from '@angular/common';
import {
  AfterContentInit,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ContentChild,
  ElementRef,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { ControlContainer, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Observable, startWith, tap } from 'rxjs';

import { ConditionalContentDirective, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';

import { existingControlContainer } from '../../providers/control-container.factory';

@Component({
  selector: 'cca-boolean-radio-group',
  template: `
    <div
      [formControlName]="controlName"
      [hint]="hint"
      [isInline]="true"
      [legend]="legend"
      legendSize="medium"
      govuk-radio
    >
      <govuk-radio-option [value]="true" [label]="yesLabel" />
      <govuk-radio-option [value]="false" [label]="noLabel" />
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
  @Input() controlName: string;
  @Input() legend: string;
  @Input() hint: string;
  @Input() isEditable = true;

  @Input() yesLabel = 'Yes';
  @Input() noLabel = 'No';

  @ViewChild(RadioComponent, { read: ElementRef, static: true }) radio: ElementRef<HTMLElement>;
  value$: Observable<boolean>;

  private yesRadio: HTMLInputElement;
  @ContentChild(ConditionalContentDirective, { static: true })
  private readonly conditional: ConditionalContentDirective;

  constructor(
    private readonly controlContainer: ControlContainer,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  get conditionalId() {
    return `${this.yesRadio?.id}-conditional`;
  }

  private get control() {
    return this.controlContainer.control.get(this.controlName);
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
    this.yesRadio = this.radio.nativeElement.querySelector('input');
    this.yesRadio.setAttribute('aria-controls', this.conditionalId);
    this.setAriaExpanded(this.control.value);

    // Trigger a change detection to update the conditionalId
    this.changeDetectorRef.detectChanges();
  }

  private onChoose(value: boolean): void {
    this.setAriaExpanded(value);

    if (this.conditional) {
      if (value && this.isEditable) {
        this.conditional.enableControls();
      } else {
        this.conditional.disableControls();
      }
    }
  }

  private setAriaExpanded(value: boolean): void {
    if (this.yesRadio) {
      this.yesRadio.setAttribute('aria-expanded', value ? 'true' : 'false');
    }
  }
}

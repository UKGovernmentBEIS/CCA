import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { TextInputComponent } from './text-input.component';

describe('TextInputComponent', () => {
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    imports: [TextInputComponent, ReactiveFormsModule],
    template: `
      <form [formGroup]="form">
        <div cca-text-input formControlName="visible" label="Visible control"></div>
        <div cca-text-input formControlName="hidden" label="Hidden control" [labelHidden]="true"></div>
        <div cca-text-input formControlName="stringFalse" label="String false control" labelHidden="false"></div>
        <div cca-text-input formControlName="placeholder"></div>
        <div cca-text-input formControlName="hinted" label="Hinted control" hint="Provide a value"></div>
      </form>
    `,
  })
  class TestComponent {
    form = new FormGroup({
      visible: new FormControl(null),
      hidden: new FormControl(null),
      stringFalse: new FormControl(null),
      placeholder: new FormControl(null),
      hinted: new FormControl(null, Validators.required),
    });
  }

  const labels = () => Array.from(fixture.nativeElement.querySelectorAll('label')) as HTMLLabelElement[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({ providers: [ControlContainer] }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.nativeElement.querySelector('input')).toBeTruthy();
  });

  it('should render the label visibly by default', () => {
    expect(labels()[0].textContent.trim()).toEqual('Visible control');
    expect(labels()[0].classList.contains('govuk-visually-hidden')).toBe(false);
  });

  it('should hide the label when labelHidden is set, keeping it in place for the accessible name', () => {
    const hiddenLabel = labels()[1];
    const forId = hiddenLabel.getAttribute('for');

    expect(hiddenLabel.textContent.trim()).toEqual('Hidden control');
    expect(hiddenLabel.classList.contains('govuk-visually-hidden')).toBe(true);
    expect(forId).toEqual('hidden');
    expect(fixture.nativeElement.querySelector(`[id="${forId}"]`)).toBeTruthy();
  });

  it('should only hide the label when labelHidden is truthy', () => {
    expect(labels()[2].classList.contains('govuk-visually-hidden')).toBe(false);
  });

  it('should describe the input with its hint and error', () => {
    const element = fixture.nativeElement as HTMLElement;
    const input = element.querySelector<HTMLInputElement>('input[name="hinted"]');
    const hint = element.querySelector<HTMLElement>(`#${input.id}-hint`);

    expect(hint).toBeTruthy();
    expect(input.getAttribute('aria-describedby')).toEqual(hint.id);

    element.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(input.getAttribute('aria-describedby')).toEqual(`${hint.id} ${input.id}-error`);
  });

  it('should hide the placeholder label when no label is given', () => {
    expect(labels()[3].classList.contains('govuk-visually-hidden')).toBe(true);
  });
});

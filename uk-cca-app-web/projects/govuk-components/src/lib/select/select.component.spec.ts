import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { SelectComponent } from './select.component';

describe('SelectComponent', () => {
  @Component({
    imports: [SelectComponent, ReactiveFormsModule],
    template: `
      <div
        govuk-select
        [options]="[
          { text: 'First', value: 1 },
          { text: 'Second', value: 2 },
        ]"
        [label]="label()"
        [labelHidden]="labelHidden()"
        [hint]="hint()"
        [formControl]="control"
      >
        <option [ngValue]="3">Third</option>
      </div>
    `,
  })
  class TestComponent {
    control = new FormControl();
    label = signal<string | undefined>(undefined);
    labelHidden = signal(false);
    hint = signal<string | undefined>(undefined);
  }

  let component: SelectComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [ControlContainer],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SelectComponent)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the options', () => {
    const options = element.querySelectorAll('option');
    expect(options).toHaveLength(3);
    expect(options[0].textContent.trim()).toEqual('First');
    expect(options[1].textContent.trim()).toEqual('Second');
    expect(options[2].textContent.trim()).toEqual('Third');
  });

  it('should disable the select', () => {
    hostComponent.control.disable();
    fixture.detectChanges();

    const select = element.querySelector<HTMLSelectElement>('select');

    expect(select.disabled).toBeTruthy();
  });

  it('should assign value', () => {
    hostComponent.control.patchValue(1);
    fixture.detectChanges();

    const select = element.querySelector<HTMLSelectElement>('select');
    expect(select.value).toEqual('0: 1');
  });

  it('emit value', () => {
    const select = element.querySelector<HTMLSelectElement>('select');
    select.value = select.options[0].value;
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    expect(hostComponent.control.value).toEqual(1);
  });

  it('should hide the label when labelHidden is set and associate the hint with the select', () => {
    hostComponent.label.set('Sort by');
    hostComponent.labelHidden.set(true);
    hostComponent.hint.set('Choose the field to sort on');
    fixture.detectChanges();

    const label = element.querySelector<HTMLLabelElement>('label');
    expect(label.textContent.trim()).toEqual('Sort by');
    expect(label.classList.contains('govuk-visually-hidden')).toBe(true);
    expect(label.getAttribute('for')).toEqual(element.querySelector('select').id);

    const hint = element.querySelector<HTMLDivElement>('.govuk-hint');
    expect(hint.textContent.trim()).toEqual('Choose the field to sort on');
    expect(element.querySelector('select').getAttribute('aria-describedby')).toEqual(hint.id);
  });

  it('should not render a hint or describe the select when no hint is given', () => {
    expect(element.querySelector('.govuk-hint')).toBeNull();
    expect(element.querySelector('select').getAttribute('aria-describedby')).toBeNull();
  });

  it('should describe the select with both the hint and the error when invalid', () => {
    hostComponent.hint.set('Choose the field to sort on');
    hostComponent.control.setValidators(Validators.required);
    hostComponent.control.updateValueAndValidity();
    fixture.detectChanges();

    const select = element.querySelector<HTMLSelectElement>('select');
    const hint = element.querySelector<HTMLDivElement>('.govuk-hint');
    const error = element.querySelector<HTMLElement>(`#${select.id}-error`);

    expect(error).toBeTruthy();
    expect(select.getAttribute('aria-describedby')).toEqual(`${hint.id} ${error.id}`);
  });
});

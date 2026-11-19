import { Component, DebugElement, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { DateInputComponent } from './date-input.component';

describe('DateInputComponent', () => {
  @Component({
    template: '<div govuk-date-input [formControl]="control" [min]="min()" [max]="max()" label="Test date"></div>',
    imports: [DateInputComponent, ReactiveFormsModule],
  })
  class TestComponent {
    control = new FormControl();
    min = signal<Date>(undefined);
    max = signal<Date>(undefined);
  }

  @Component({
    template: `
      <form [formGroup]="form">
        @if (showDateInput()) {
          <div govuk-date-input formControlName="date"></div>
        }
      </form>
    `,
    imports: [DateInputComponent, ReactiveFormsModule],
  })
  class Test2Component {
    control = new FormControl(null, Validators.required);
    form = new FormGroup({ date: this.control });
    showDateInput = signal(true);
  }

  let component: DateInputComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let fixture2: ComponentFixture<Test2Component>;

  const changeValue = (input: DebugElement, value: string) => {
    input.triggerEventHandler('input', { target: { value } });
    input.triggerEventHandler('blur', { target: { value } });
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, TestComponent, Test2Component],
      providers: [ControlContainer],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    fixture2 = TestBed.createComponent(Test2Component);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(DateInputComponent)).componentInstance;
    fixture.detectChanges();
    fixture2.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('removes date errors from a surviving control and parent form without emitting on teardown', () => {
    const host = fixture2.componentInstance;
    const inputs = fixture2.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '31');
    fixture2.detectChanges();
    expect(host.control.hasError('isIncomplete')).toBe(true);
    expect(host.control.hasValidator(Validators.required)).toBe(true);

    const valueChanged = vi.fn();
    const statusChanged = vi.fn();
    const parentValueChanged = vi.fn();
    const parentStatusChanged = vi.fn();
    host.control.valueChanges.subscribe(valueChanged);
    host.control.statusChanges.subscribe(statusChanged);
    host.form.valueChanges.subscribe(parentValueChanged);
    host.form.statusChanges.subscribe(parentStatusChanged);

    host.showDateInput.set(false);
    fixture2.detectChanges();

    expect(host.control.errors).toEqual({ required: true });
    expect(host.control.hasValidator(Validators.required)).toBe(true);
    expect(host.form.invalid).toBe(true);
    expect(valueChanged).not.toHaveBeenCalled();
    expect(statusChanged).not.toHaveBeenCalled();
    expect(parentValueChanged).not.toHaveBeenCalled();
    expect(parentStatusChanged).not.toHaveBeenCalled();

    host.control.setValue(new Date('2025-06-01'));
    expect(host.control.valid).toBe(true);
    expect(host.form.valid).toBe(true);
  });

  it('removes its only validator when the whole view is destroyed', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '31');
    fixture.detectChanges();
    const parent = new FormGroup({ date: hostComponent.control });
    expect(parent.invalid).toBe(true);

    fixture.destroy();

    expect(hostComponent.control.validator).toBeNull();
    expect(hostComponent.control.errors).toBeNull();
    expect(parent.valid).toBe(true);
  });

  it('preserves validators added by the consumer while the input is mounted', () => {
    const host = fixture2.componentInstance;
    const addedValidator = () => ({ added: true });
    host.control.addValidators(addedValidator);
    const inputs = fixture2.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '31');
    fixture2.detectChanges();
    expect(host.control.hasError('isIncomplete')).toBe(true);
    expect(host.control.hasError('added')).toBe(true);

    host.showDateInput.set(false);
    fixture2.detectChanges();

    expect(host.control.hasValidator(Validators.required)).toBe(true);
    expect(host.control.hasValidator(addedValidator)).toBe(true);
    expect(host.control.errors).toEqual({ required: true, added: true });
  });

  it('allows the consumer to remove an original validator while the input is mounted', () => {
    const host = fixture2.componentInstance;
    host.control.removeValidators(Validators.required);
    host.control.updateValueAndValidity();
    expect(host.control.valid).toBe(true);

    host.showDateInput.set(false);
    fixture2.detectChanges();

    expect(host.control.validator).toBeNull();
    expect(host.control.valid).toBe(true);
  });

  it('validates fresh date inputs after repeated conditional destruction and recreation', () => {
    const host = fixture2.componentInstance;

    for (let visit = 0; visit < 3; visit++) {
      const inputs = fixture2.debugElement.queryAll(By.css('input'));
      changeValue(inputs[0], '31');
      changeValue(inputs[1], '2');
      changeValue(inputs[2], '2025');
      fixture2.detectChanges();
      expect(host.control.hasError('isUnrealDate')).toBe(true);

      host.showDateInput.set(false);
      fixture2.detectChanges();
      host.control.setValue(new Date('2025-06-01'));
      expect(host.control.valid).toBe(true);

      host.showDateInput.set(true);
      fixture2.detectChanges();
      const restoredInputs = fixture2.nativeElement.querySelectorAll('input');
      expect(Array.from(restoredInputs, (input: HTMLInputElement) => input.value)).toEqual(['1', '6', '2025']);
      expect(host.control.value).toEqual(new Date('2025-06-01'));
      expect(host.form.valid).toBe(true);
    }
  });

  it.each([
    ['31', '2', '2025'],
    ['29', '2', '2025'],
    ['31', '4', '2025'],
    ['31', '', ''],
  ])('does not retain or restore an invalid date entered as %s/%s/%s', (day, month, year) => {
    const host = fixture2.componentInstance;
    const inputs = fixture2.debugElement.queryAll(By.css('input'));
    [day, month, year].forEach((value, index) => changeValue(inputs[index], value));
    fixture2.detectChanges();

    expect(host.control.invalid).toBe(true);
    expect(host.control.value).toBeNull();

    host.showDateInput.set(false);
    fixture2.detectChanges();

    expect(host.control.value).toBeNull();
    expect(host.control.errors).toEqual({ required: true });

    host.showDateInput.set(true);
    fixture2.detectChanges();

    const restoredInputs = fixture2.nativeElement.querySelectorAll('input');
    expect(Array.from(restoredInputs, (input: HTMLInputElement) => input.value)).toEqual(['', '', '']);
    expect(host.control.value).toBeNull();
    expect(host.form.invalid).toBe(true);
  });

  it('preserves validators cleared by the consumer before the input is destroyed', () => {
    const host = fixture2.componentInstance;
    host.control.clearValidators();
    host.control.updateValueAndValidity();
    host.showDateInput.set(false);
    fixture2.detectChanges();

    expect(host.control.validator).toBeNull();
    expect(host.control.valid).toBe(true);
  });

  it('preserves replacement validators assigned by the consumer before the input is destroyed', () => {
    const host = fixture2.componentInstance;
    const replacementValidator = () => ({ replacement: true });
    host.control.setValidators(replacementValidator);
    host.control.updateValueAndValidity();
    host.showDateInput.set(false);
    fixture2.detectChanges();

    expect(host.control.validator).toBe(replacementValidator);
    expect(host.control.errors).toEqual({ replacement: true });
  });

  it('should render a legend when a label is provided', () => {
    const legend = fixture.nativeElement.querySelector('legend');
    expect(legend).not.toBeNull();
    expect(legend.textContent.trim()).toEqual('Test date');
  });

  it('should not render a legend when no label is provided', () => {
    expect(fixture2.nativeElement.querySelector('legend')).toBeNull();
  });

  it('should hydrate form with a date', () => {
    hostComponent.control.patchValue(new Date('2020-05-29'));
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const inputs = hostElement.querySelectorAll<HTMLInputElement>('input');

    expect(Array.from(inputs).map((input) => input.value)).toEqual(['29', '5', '2020']);
  });

  it('should emit events on user input', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '29');

    fixture.detectChanges();

    expect(hostComponent.control.value).toBeNull();

    changeValue(inputs[1], '5');
    changeValue(inputs[2], '2020');

    fixture.detectChanges();
    expect(hostComponent.control.value.toDateString()).toEqual(new Date('2020-05-29').toDateString());
  });

  it('should disable/enable the inputs', () => {
    hostComponent.control.disable();
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    let inputs = hostElement.querySelectorAll<HTMLInputElement>('input');

    inputs.forEach((input) => expect(input.disabled).toBeTruthy());

    hostComponent.control.enable();
    fixture.detectChanges();

    inputs = hostElement.querySelectorAll<HTMLInputElement>('input');

    inputs.forEach((input) => expect(input.disabled).toBeFalsy());
  });

  it('should set as touched only if every input is touched', () => {
    const hostElement = fixture.debugElement;
    const inputs = hostElement.queryAll(By.css('input'));

    inputs[0].triggerEventHandler('focus', {});
    inputs[0].triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(hostComponent.control.touched).toBeFalsy();

    inputs[1].triggerEventHandler('focus', {});
    inputs[1].triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(hostComponent.control.touched).toBeFalsy();

    inputs[2].triggerEventHandler('focus', {});
    inputs[2].triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(hostComponent.control.touched).toBeTruthy();
  });

  it('should not accept 29 Feb of leap year', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '29');
    changeValue(inputs[1], '2');
    changeValue(inputs[2], '2019');
    fixture.detectChanges();

    expect(hostComponent.control.invalid).toBeTruthy();
    let errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).not.toBeNull();

    changeValue(inputs[2], '2020');
    fixture.detectChanges();
    expect(hostComponent.control.valid).toBeTruthy();
    expect(hostComponent.control.value.toDateString()).toEqual(new Date('2020-02-29').toDateString());
    errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).toBeNull();
  });

  it('should not accept 30 Feb of any year', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '30');
    changeValue(inputs[1], '2');
    changeValue(inputs[2], '2019');
    fixture.detectChanges();

    expect(hostComponent.control.valid).not.toBeTruthy();

    changeValue(inputs[2], '2020');
    fixture.detectChanges();
    expect(hostComponent.control.valid).not.toBeTruthy();

    fixture.detectChanges();
    const errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).not.toBeNull();
  });

  it('should not accept 31 of short months', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '31');
    changeValue(inputs[1], '4');
    changeValue(inputs[2], '2020');
    fixture.detectChanges();

    expect(hostComponent.control.valid).not.toBeTruthy();

    let errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).not.toBeNull();

    changeValue(inputs[1], '5');
    fixture.detectChanges();
    expect(hostComponent.control.valid).toBeTruthy();
    expect(hostComponent.control.value.toDateString()).toEqual(new Date('2020-05-31').toDateString());
    errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).toBeNull();
  });

  it('should show incomplete errors', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '31');
    fixture.detectChanges();

    expect(hostComponent.control.valid).not.toBeTruthy();
    expect(hostComponent.control.errors.isIncomplete).toBeTruthy();
    const errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).not.toBeNull();
  });

  it('should show min errors', async () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '31');
    changeValue(inputs[1], '3');
    changeValue(inputs[2], '2020');
    fixture.detectChanges();

    expect(hostComponent.control.valid).toBeTruthy();
    expect(hostComponent.control.value.toDateString()).toEqual(new Date('2020-03-31').toDateString());
    let errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).toBeNull();

    hostComponent.min.set(new Date('2020-05-31'));
    fixture.detectChanges();

    expect(hostComponent.control.valid).not.toBeTruthy();
    expect(hostComponent.control.errors.minDate).toBeTruthy();
    expect(hostComponent.control.value).toEqual(new Date('2020-03-31'));
    errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).not.toBeNull();
  });

  it('should show max errors', async () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[0], '31');
    changeValue(inputs[1], '3');
    changeValue(inputs[2], '2020');
    fixture.detectChanges();

    expect(hostComponent.control.valid).toBeTruthy();
    expect(hostComponent.control.value.toDateString()).toEqual(new Date('2020-03-31').toDateString());
    let errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).toBeNull();

    hostComponent.max.set(new Date('2018-05-31'));
    fixture.detectChanges();

    expect(hostComponent.control.valid).not.toBeTruthy();
    expect(hostComponent.control.errors.maxDate).toBeTruthy();
    expect(hostComponent.control.value).toEqual(new Date('2020-03-31'));
    errorMessage = fixture.nativeElement.querySelector('.govuk-error-message');
    expect(errorMessage).not.toBeNull();
  });

  it('highlights all date parts for a consumer date error without a max bound', () => {
    hostComponent.control.addValidators(() => ({ invalidDate: 'This date cannot be in the future' }));
    hostComponent.control.setValue(new Date('2099-01-01'));
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('input.govuk-input--error')).toHaveLength(3);
    const errors = fixture.nativeElement.querySelectorAll('.govuk-error-message > span');
    expect(errors).toHaveLength(1);
    expect(errors[0].textContent).toContain('This date cannot be in the future');
  });

  it('compares inclusive bounds as calendar dates and displays their UTC calendar day', () => {
    hostComponent.min.set(new Date('2025-07-15T12:00:00Z'));
    hostComponent.max.set(new Date('2025-07-16T12:00:00Z'));
    hostComponent.control.setValue(new Date('2025-07-15'));
    fixture.detectChanges();
    expect(hostComponent.control.valid).toBe(true);

    hostComponent.control.setValue(new Date('2025-07-14'));
    fixture.detectChanges();
    expect(hostComponent.control.errors).toEqual({
      minDate: 'This date must be the same as or after 15 July 2025',
    });

    hostComponent.control.setValue(new Date('2025-07-16'));
    fixture.detectChanges();
    expect(hostComponent.control.valid).toBe(true);

    hostComponent.max.set(new Date('2025-07-16'));
    hostComponent.control.setValue(new Date('2025-07-17'));
    fixture.detectChanges();
    expect(hostComponent.control.errors).toEqual({
      maxDate: 'This date must be the same as or before 16 July 2025',
    });
  });

  it.each([
    ['14', 'minDate'],
    ['16', 'maxDate'],
  ])('preserves a real date with a %s July day that fails the %s bound', (day, error) => {
    hostComponent.min.set(new Date('2025-07-15'));
    hostComponent.max.set(new Date('2025-07-15'));
    fixture.detectChanges();

    const inputs = fixture.debugElement.queryAll(By.css('input'));
    [day, '7', '2025'].forEach((value, index) => changeValue(inputs[index], value));
    fixture.detectChanges();

    expect(hostComponent.control.hasError(error)).toBe(true);
    expect(hostComponent.control.value).toEqual(new Date(`2025-07-${day}`));
  });

  it('should not accept month value over 12', () => {
    const inputs = fixture.debugElement.queryAll(By.css('input'));
    changeValue(inputs[1], '15');
    fixture.detectChanges();

    expect(hostComponent.control.valid).toBeFalsy();
  });
});

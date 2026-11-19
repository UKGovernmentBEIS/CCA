import { provideRouter } from '@angular/router';
import { Component, signal, viewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, FormsModule, NgForm, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { GovukValidators } from '../error-message';
import { TextInputComponent } from '../text-input';
import { ErrorSummaryComponent } from './error-summary.component';

describe('ErrorSummaryComponent', () => {
  @Component({
    template: `
      @if (!isTemplate()) {
        <form [formGroup]="form">
          <govuk-error-summary [form]="form" />
          <div govuk-text-input inputType="text" formControlName="topLevel"></div>
          <div formGroupName="secondLevelTop">
            <div govuk-text-input inputType="text" formControlName="secondLevelFirst"></div>
            <div govuk-text-input inputType="text" formControlName="secondLevelSecond"></div>
          </div>

          <div formArrayName="secondLevelArrayTop">
            <div [formGroupName]="0">
              <div govuk-text-input inputType="text" formControlName="nestedArrayControl"></div>
            </div>
            <div govuk-text-input inputType="text" [formControlName]="1"></div>
          </div>
        </form>
      }

      @if (isTemplate()) {
        <form #templateForm="ngForm">
          <govuk-error-summary [form]="templateForm" />
          <select [(ngModel)]="selectValue" name="someField" required></select>
        </form>
      }
    `,
    imports: [ErrorSummaryComponent, ReactiveFormsModule, TextInputComponent, FormsModule],
  })
  class TestComponent {
    public readonly testForm = viewChild<NgForm>('templateForm');

    form: FormGroup;
    isTemplate = signal(false);
    selectValue: any;
  }

  let component: ErrorSummaryComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  const reactiveForm = new FormGroup({
    topLevel: new FormControl(null, GovukValidators.required('Enter topLevel')),
    secondLevelTop: new FormGroup({
      secondLevelFirst: new FormControl(null, GovukValidators.required('Enter secondLevelFirst')),
      secondLevelSecond: new FormControl(null, GovukValidators.required('Enter secondLevelSecond')),
    }),
    secondLevelArrayTop: new FormArray([
      new FormGroup({
        nestedArrayControl: new FormControl(null, GovukValidators.required('Enter nestedArrayControl')),
      }),
      new FormControl(null, GovukValidators.required('Enter arrayControl')),
    ]),
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    hostComponent.form = reactiveForm;
    fixture.detectChanges();
    component = fixture.debugElement.query(By.directive(ErrorSummaryComponent)).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display reactive form errors', async () => {
    hostComponent.form = reactiveForm;
    hostComponent.form.markAllAsTouched();
    // TODO: revisit this workaround and replace with a cleaner update path if NG0100 handling changes.
    fixture.changeDetectorRef.markForCheck();
    await fixture.whenStable();

    const hostElement: HTMLElement = fixture.nativeElement;
    const errors = hostElement.querySelectorAll('a');

    expect(errors.length).toEqual(5);
    expect(Array.from(errors).map((error) => error.textContent.trim())).toEqual([
      'Enter topLevel',
      'Enter secondLevelFirst',
      'Enter secondLevelSecond',
      'Enter nestedArrayControl',
      'Enter arrayControl',
    ]);
  });

  it('should display template form errors', async () => {
    hostComponent.isTemplate.set(true);
    await fixture.whenStable();
    hostComponent.testForm().control.markAllAsTouched();

    const hostElement: HTMLElement = fixture.nativeElement;
    expect(hostElement.querySelectorAll<HTMLAnchorElement>('a').length).toEqual(1);
  });

  it.each([
    [{ required: 'Enter the payment date', isIncomplete: 'Enter a full date' }, ['Enter a full date']],
    [{ required: 'Enter the payment date', isUnrealDate: 'Enter a real date' }, ['Enter a real date']],
    [{ required: 'Enter the payment date', isEmpty: 'Enter a date' }, ['Enter the payment date']],
    [
      { required: 'Enter the payment date', isUnrealDate: 'Enter a real date', external: 'Another problem' },
      ['Another problem', 'Enter a real date'],
    ],
    [{ required: 'Enter a value', external: 'Another problem' }, ['Another problem', 'Enter a value']],
  ])('summarises specific date errors without altering validation: %j', (errors, expectedMessages) => {
    const summaryFixture = TestBed.createComponent(ErrorSummaryComponent);
    const control = new FormControl();
    const form = new FormGroup({ date: control });
    const originalErrors = { ...errors };
    control.setErrors(errors);
    summaryFixture.componentRef.setInput('form', form);
    summaryFixture.detectChanges();

    const messages = summaryFixture.nativeElement.querySelectorAll('.govuk-error-summary__list a');
    expect(Array.from(messages, (message: HTMLElement) => message.textContent.trim())).toEqual(expectedMessages);
    expect(control.errors).toEqual(originalErrors);
    expect(control.invalid).toBe(true);
    summaryFixture.destroy();
  });

  it('should prefix the title with Error', () => {
    hostComponent.form = reactiveForm;
    hostComponent.form.markAllAsTouched();

    expect(document.title).toContain('Error');
  });

  it('should render the error summary heading as an h2', async () => {
    hostComponent.form = reactiveForm;
    hostComponent.form.markAllAsTouched();
    fixture.changeDetectorRef.markForCheck();
    await fixture.whenStable();

    const heading = fixture.nativeElement.querySelector('.govuk-error-summary__title');
    expect(heading.tagName).toBe('H2');
    expect(heading.textContent.trim()).toBe('There is a problem');
  });

  it('should focus on error container', () => {
    hostComponent.form = reactiveForm;
    hostComponent.form.markAllAsTouched();

    const hostElement: HTMLElement = fixture.nativeElement;
    const errorContainer = hostElement.querySelector<HTMLDivElement>('.govuk-error-summary');
    expect(document.activeElement).toBe(errorContainer);
  });

  it('should move focus back to an already mounted summary when the form is submitted with errors', async () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const input = hostElement.querySelector<HTMLInputElement>('input');
    input.focus();
    expect(document.activeElement).toBe(input);

    hostElement.querySelector('form').dispatchEvent(new Event('submit'));
    await fixture.whenStable();

    expect(document.activeElement).toBe(hostElement.querySelector('.govuk-error-summary'));
  });

  it('should move focus back to an already mounted summary when focus() is called', async () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const input = hostElement.querySelector<HTMLInputElement>('input');
    input.focus();

    component.focus();
    await fixture.whenStable();

    expect(document.activeElement).toBe(hostElement.querySelector('.govuk-error-summary'));
  });

  it('should not move focus when the form changes without a submit', async () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const input = hostElement.querySelector<HTMLInputElement>('input');
    input.focus();

    reactiveForm.get('topLevel').setValue('');
    await fixture.whenStable();

    expect(document.activeElement).toBe(input);
  });
});

import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TextInputComponent } from '@netz/govuk-components';
import { Mock } from 'vitest';

import { WizardStepComponent } from './wizard-step.component';

describe('WizardStepComponent', () => {
  let component: WizardStepComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <cca-wizard-step
        [formGroup]="formGroup"
        heading="Some form"
        caption="Some caption"
        (formSubmit)="onSubmit($event)"
      >
        <div govuk-text-input formControlName="date" label="Date"></div>
        <div govuk-text-input formControlName="text" label="Text"></div>
      </cca-wizard-step>
    `,
    imports: [WizardStepComponent, ReactiveFormsModule, TextInputComponent],
  })
  class TestComponent {
    formGroup = new FormGroup({
      date: new FormControl('', [Validators.required]),
      text: new FormControl(''),
    });
    onSubmit: ((form: FormGroup) => void) | Mock<(form: FormGroup) => void>;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageHeadingComponent, TestComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(WizardStepComponent)).componentInstance;
    hostComponent.onSubmit = vi.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(element.querySelector('.govuk-caption-l')).toBeTruthy();
    expect(element.querySelector('.govuk-heading-l')).toBeTruthy();
    expect(element.querySelector('button[type="submit"]')).toBeTruthy();
    expect(element.querySelectorAll('input').length).toEqual(2);
  });

  it('should render errors on submit', () => {
    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();

    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();

    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(hostComponent.onSubmit).not.toHaveBeenCalled();
  });

  it('should move focus back to the error summary on a second failed submit', async () => {
    const submitButton = element.querySelector<HTMLButtonElement>('button[type="submit"]');
    submitButton.click();
    await fixture.whenStable();

    const input = element.querySelector('input');
    input.focus();
    submitButton.click();
    await fixture.whenStable();

    expect(document.activeElement).toBe(element.querySelector('.govuk-error-summary'));
  });

  it('should show and refocus the error summary for errors set after a valid submit', async () => {
    hostComponent.formGroup.patchValue({ date: '01/01/2026' });
    const submitButton = element.querySelector<HTMLButtonElement>('button[type="submit"]');
    submitButton.click();
    await fixture.whenStable();
    expect(hostComponent.onSubmit).toHaveBeenCalledTimes(1);

    hostComponent.formGroup.controls.text.setErrors({ apiError: 'Server rejected the value' });
    component.showErrorSummary();
    await fixture.whenStable();
    expect(document.activeElement).toBe(element.querySelector('.govuk-error-summary'));

    hostComponent.formGroup.controls.text.setValue('changed');
    submitButton.focus();
    submitButton.click();
    await fixture.whenStable();
    expect(hostComponent.onSubmit).toHaveBeenCalledTimes(2);

    hostComponent.formGroup.controls.text.setErrors({ apiError: 'Server rejected the value again' });
    component.showErrorSummary();
    await fixture.whenStable();

    expect(document.activeElement).toBe(element.querySelector('.govuk-error-summary'));
  });
});

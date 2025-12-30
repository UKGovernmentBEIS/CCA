import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormControl, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule, By } from '@angular/platform-browser';
import { Component } from '@angular/core';

import { ErrorMessageComponent } from '../error-message';
import { GovukValidators } from '../error-message';
import { TextareaComponent } from './textarea.component';

describe('TextareaComponent', () => {
  @Component({
    imports: [TextareaComponent, ReactiveFormsModule],
    template: `
      <div govuk-textarea [formControl]="control" [maxLength]="maxLength" [label]="label" [labelSize]="labelSize"></div>
    `,
  })
  class TestComponent {
    control = new FormControl();
    maxLength: number;
    label: string;
    labelSize: string;
  }

  let component: TextareaComponent;
  let hostComponent: TestComponent;
  let hostComponentFixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, BrowserModule, TextareaComponent, TestComponent, ErrorMessageComponent],
      providers: [ControlContainer],
    }).compileComponents();

    hostComponentFixture = TestBed.createComponent(TestComponent);
    hostComponent = hostComponentFixture.componentInstance;
    component = hostComponentFixture.debugElement.query(By.directive(TextareaComponent)).componentInstance;
    hostComponentFixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable the textarea', () => {
    component.control.disable();
    hostComponentFixture.detectChanges();

    const hostElement: HTMLElement = hostComponentFixture.nativeElement;
    const textarea = hostElement.querySelector<HTMLTextAreaElement>('textarea');

    expect(textarea.disabled).toBeTruthy();
  });

  it('should assign value', () => {
    const stringValue = 'This is a test';
    component.control.patchValue(stringValue);
    hostComponentFixture.detectChanges();

    const hostElement: HTMLElement = hostComponentFixture.nativeElement;
    const textarea = hostElement.querySelector<HTMLTextAreaElement>('textarea');
    expect(textarea.value).toEqual(stringValue);
  });

  it('should emit value', () => {
    const stringValue = ' This is a test \n Test \n\n';

    expect(component.control.value).toBeNull();

    component.control.patchValue(stringValue);

    expect(component.control.value).toEqual('This is a test \n Test');
  });

  it('should show character count info and error', () => {
    const element: HTMLElement = hostComponentFixture.nativeElement;
    expect(element.querySelector('.govuk-character-count__message')).toBeNull();

    hostComponent.maxLength = 10;
    component.control.clearValidators();
    component.control.setValidators(GovukValidators.maxLength(10, 'no more than 10'));
    component.control.updateValueAndValidity();
    hostComponentFixture.detectChanges();

    expect(element.querySelector('.govuk-character-count__message').textContent.trim()).toEqual('');
    expect(element.querySelector('.govuk-character-count__message.govuk-error-message')).toBeNull();

    const withinLimit = '1234567890';
    component.control.setValue(withinLimit);
    hostComponentFixture.detectChanges();

    expect(element.querySelector('.govuk-character-count__message').textContent.trim()).toEqual(
      'You have 0 characters remaining',
    );
    expect(element.querySelector('.govuk-character-count__message.govuk-error-message')).toBeNull();

    const stringValue = '12345678901';
    component.control.setValue(stringValue);
    hostComponentFixture.detectChanges();

    expect(element.querySelector('.govuk-character-count__message.govuk-error-message')).toBeTruthy();
    expect(element.querySelector('.govuk-character-count__message.govuk-error-message').textContent.trim()).toEqual(
      'You have 1 character too many',
    );
    expect(element.querySelector('.govuk-form-group--error')).toBeTruthy();
    expect(hostComponentFixture.nativeElement.querySelectorAll('.govuk-error-message')[0].textContent).toContain(
      'no more than 10',
    );
  });

  it('should display labelSize classes', () => {
    const hostElement: HTMLElement = hostComponentFixture.nativeElement;
    const label = hostElement.querySelector('label');

    expect(label.className).toEqual('govuk-label govuk-visually-hidden');

    hostComponent.labelSize = 'normal';
    hostComponentFixture.detectChanges();

    expect(label.className).toEqual('govuk-label govuk-visually-hidden');

    hostComponent.labelSize = 'small';
    hostComponentFixture.detectChanges();

    expect(label.className).toEqual('govuk-label govuk-visually-hidden govuk-label--s');

    hostComponent.labelSize = 'medium';
    hostComponentFixture.detectChanges();

    expect(label.className).toEqual('govuk-label govuk-visually-hidden govuk-label--m');

    hostComponent.labelSize = 'large';
    hostComponentFixture.detectChanges();

    expect(label.className).toEqual('govuk-label govuk-visually-hidden govuk-label--l');
  });
});

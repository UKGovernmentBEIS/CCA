import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule, ValidationErrors } from '@angular/forms';

import { of } from 'rxjs';

import { BasePage } from '@netz/common/testing';

import { CsvErrorSummaryComponent } from './csv-error-summary.component';
import { NestedMessageValidationError } from './nested-message-validation-error.interface';

describe('CsvErrorSummaryComponent', () => {
  let component: CsvErrorSummaryComponent;
  let fixture: ComponentFixture<CsvErrorSummaryComponent>;
  let page: Page;

  class Page extends BasePage<CsvErrorSummaryComponent> {
    get summaries() {
      return this.queryAll<HTMLParagraphElement>('li p').map((el) => el.textContent.trim());
    }
  }

  beforeEach(() => {
    fixture = TestBed.createComponent(CsvErrorSummaryComponent);
    component = fixture.componentInstance;
    component.errorList$ = of([
      {
        message: 'Test message',
        columns: ['Test columns'],
        rows: [
          {
            rowIndex: 1,
          },
          {
            rowIndex: 2,
          },
        ],
      },
    ] as NestedMessageValidationError[]);

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.summaries).toEqual(['Test message', "Check the data in column 'Test columns' on rows 1, 2"]);
  });
});

describe('CsvErrorSummaryComponent focus', () => {
  let resolveValidation: (errors: ValidationErrors | null) => void;

  @Component({
    template: `
      <form [formGroup]="form">
        <cca-csv-error-summary [form]="form" />
        <input formControlName="file" />
      </form>
    `,
    imports: [ReactiveFormsModule, CsvErrorSummaryComponent],
  })
  class HostComponent {
    readonly form = new FormGroup({
      file: new FormControl(null, {
        asyncValidators: () => new Promise<ValidationErrors | null>((resolve) => (resolveValidation = resolve)),
      }),
    });
  }

  it('should move focus to an already mounted summary once async validation settles after a submit', async () => {
    const fixture = TestBed.createComponent(HostComponent);
    // The validator promise settles in a microtask after whenStable has already resolved.
    const settle = async () => {
      await new Promise((resolve) => setTimeout(resolve));
      await fixture.whenStable();
    };
    await fixture.whenStable();
    fixture.componentInstance.form.controls.file.setValue('first');
    resolveValidation({ csvInvalid: { message: 'First problem' } });
    await settle();

    const hostElement: HTMLElement = fixture.nativeElement;
    expect(hostElement.querySelector('.govuk-error-summary')).not.toBeNull();
    const input = hostElement.querySelector('input');
    input.focus();

    fixture.componentInstance.form.controls.file.setValue('second');
    hostElement.querySelector('form').dispatchEvent(new Event('submit'));
    await fixture.whenStable();
    expect(document.activeElement).toBe(input);

    resolveValidation({ csvInvalid: { message: 'Second problem' } });
    await settle();

    expect(document.activeElement).toBe(hostElement.querySelector('.govuk-error-summary'));
  });
});

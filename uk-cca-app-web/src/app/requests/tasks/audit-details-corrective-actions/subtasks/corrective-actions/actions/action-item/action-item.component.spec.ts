import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { DateInputComponent, TextareaComponent } from '@netz/govuk-components';

const mockForm = new FormGroup({
  title: new FormControl(null),
  details: new FormControl(null),
  deadline: new FormControl(new Date()),
});

@Component({
  selector: 'cca-test-wrapper',
  template: `
    <form [formGroup]="form">
      <h3 class="govuk-heading-m">Corrective action 1</h3>

      <div
        govuk-textarea
        label="Corrective action details"
        labelSize="medium"
        hint="Provide details of the actions the operator must complete"
        formControlName="details"
      ></div>

      <div
        formControlName="deadline"
        govuk-date-input
        label="Corrective action deadline"
        size="normal"
        class="govuk-!-margin-bottom-9 govuk-!-margin-top-9"
      ></div>
    </form>
  `,
  imports: [ReactiveFormsModule, TextareaComponent, DateInputComponent],
})
class TestWrapperComponent {
  form = mockForm;
}

describe('ActionItemComponent', () => {
  let component: TestWrapperComponent;
  let fixture: ComponentFixture<TestWrapperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestWrapperComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct heading', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Corrective action 1');
  });

  it('should display corrective action details label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Corrective action details');
  });

  it('should display corrective action deadline label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Corrective action deadline');
  });
});

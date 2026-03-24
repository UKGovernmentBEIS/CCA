import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByLabelText, queryByText } from '@testing';

import { mockUNAReviewRequestTaskState } from '../testing';
import { DecisionComponent } from './decision.component';
import { DECISION_FORM_PROVIDER, decisionFormProvider } from './provider';
import { DecisionWithDateFormModel } from './type';

@Component({
  selector: 'cca-test',
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <cca-decision />
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, DecisionComponent],
  providers: [decisionFormProvider('AUTHORISATION_AND_ADDITIONAL_EVIDENCE')],
})
class TestComponent {
  form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);

  onSubmit() {
    // Form submission handler
  }
}

describe('decision with date test', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ taskId: '1' }) },
      ],
    }).compileComponents();

    const store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);

    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
  });

  it('should display controls', () => {
    expect(getByLabelText('Accepted')).toBeTruthy();
    expect(getByLabelText('Rejected')).toBeTruthy();
    expect(getByLabelText('Notes (optional)')).toBeTruthy();
    expect(getByLabelText('Upload evidence (optional)')).toBeTruthy();
  });

  it('should show prepopulate form', async () => {
    expect((getByLabelText('Accepted') as HTMLInputElement | null)?.checked ?? false).toBe(true);
    expect((getByLabelText('Notes (optional)') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '').toBe(
      'asdasd',
    );
  });

  it('should immediately show error on form submission in a real scenario', async () => {
    const debugElement = fixture.debugElement;
    const component = debugElement.componentInstance;
    const cdr = debugElement.injector.get(ChangeDetectorRef);

    // Clear the pre-populated decision to trigger validation error
    component.form.patchValue({ type: null });

    // Simulate form submission
    const formElement = debugElement.query(By.css('form'));
    formElement.nativeElement.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));

    // Force change detection
    cdr.markForCheck();
    fixture.detectChanges();

    // Error should be visible
    const errorElement = queryByText(
      /Select the option that corresponds to your decision on the information above\./,
      fixture.nativeElement,
    );
    expect(errorElement).not.toBeNull();
  });
});

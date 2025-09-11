import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { RequestTaskStore } from '@netz/common/store';
import { render, RenderResult } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

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
  standalone: true,
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
  let fixture: RenderResult<TestComponent>;

  beforeEach(async () => {
    fixture = await render(TestComponent, {
      providers: [provideHttpClient(), provideHttpClientTesting(), RequestTaskStore],
      configureTestBed: (testbed) => {
        const store = testbed.inject(RequestTaskStore);
        store.setState(mockUNAReviewRequestTaskState);
      },
    });
  });

  it('should display controls', () => {
    expect(screen.getByLabelText('Accepted')).toBeInTheDocument();
    expect(screen.getByLabelText('Rejected')).toBeInTheDocument();
    expect(screen.getByLabelText('Notes (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Upload evidence (optional)')).toBeInTheDocument();
  });

  it('should show prepopulate form', async () => {
    expect(screen.getByLabelText('Accepted')).toBeChecked();
    expect(screen.getByLabelText('Notes (optional)')).toHaveValue('asdasd');
  });

  it('should immediately show error on form submission in a real scenario', async () => {
    const { debugElement } = fixture;
    const component = debugElement.componentInstance;
    const cdr = debugElement.injector.get(ChangeDetectorRef);

    // Clear the pre-populated decision to trigger validation error
    component.form.patchValue({ type: null });

    // Simulate form submission
    const formElement = debugElement.query(By.css('form'));
    formElement.nativeElement.dispatchEvent(new Event('submit'));

    // Force change detection
    cdr.markForCheck();
    fixture.detectChanges();

    // Error should be visible
    const errorElement = screen.queryByText(
      'Select the option that corresponds to your decision on the information above.',
    );
    expect(errorElement).toBeInTheDocument();
  });
});

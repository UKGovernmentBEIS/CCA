import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockUNAReviewRequestTaskState } from '../../testing';
import { DecisionComponent } from './decision.component';
import { DECISION_FORM_PROVIDER, decisionFormProvider } from './decision-form.provider';
import { DecisionWithDateFormModel } from './decision-form.type';

@Component({
  selector: 'cca-test',
  template: `
    <form [formGroup]="form">
      <cca-decision></cca-decision>
    </form>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, DecisionComponent],
  providers: [decisionFormProvider('AUTHORISATION_AND_ADDITIONAL_EVIDENCE')],
})
class TestComponent {
  form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);
}
describe('decision with date test', () => {
  beforeEach(async () => {
    await render(TestComponent, {
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
    expect(screen.getByLabelText('Notes')).toBeInTheDocument();
    expect(screen.getByLabelText('Upload evidence (optional)')).toBeInTheDocument();
  });
  it('should show prepopulate form', async () => {
    expect(screen.getByLabelText('Accepted')).toBeChecked();
    expect(screen.getByLabelText('Notes')).toHaveValue('asdasd');
  });
});

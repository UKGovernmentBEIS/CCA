import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskItemUNAReviewDTO } from '../../testing';
import { DECISION_FORM_PROVIDER, facilityDecisionFormProvider } from '../provider';
import { DecisionWithDateFormModel } from '../type';
import { DecisionWithDateComponent } from './decision-with-date.component';

@Component({
  selector: 'cca-test',
  template: `
    <form [formGroup]="form">
      <cca-decision-with-date />
    </form>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, DecisionWithDateComponent],
  providers: [facilityDecisionFormProvider()],
})
class TestComponent {
  form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);
}

describe('decision with date test', () => {
  const route: any = {
    snapshot: {
      params: {
        facilityId: 'ADS_53-F00007',
      },
      pathFromRoot: [],
    },
  };

  beforeEach(async () => {
    await render(TestComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
      ],
      configureTestBed: (testbed) => {
        const store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(mockRequestTaskItemUNAReviewDTO);
      },
    });
  });

  it('should display controls', () => {
    expect(screen.getByLabelText('Accepted')).toBeInTheDocument();
    expect(screen.getByLabelText('Rejected')).toBeInTheDocument();
    expect(screen.getByLabelText('Notes (optional)')).toBeInTheDocument();
    expect(screen.getByLabelText('Upload evidence (optional)')).toBeInTheDocument();
  });

  it('should show checkbox and date input when accepted is clicked', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByLabelText('Accepted'));
    expect(document.getElementById('changeDate-0')).toBeVisible();
    await user.click(document.getElementById('changeDate-0'));
    expect(document.getElementById('startDate')).toBeVisible();
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { click, getByLabelText } from '@testing';

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
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, DecisionWithDateComponent],
  providers: [facilityDecisionFormProvider()],
})
class TestComponent {
  form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);
}

describe('decision with date test', () => {
  let fixture: ComponentFixture<TestComponent>;

  const route: any = {
    snapshot: {
      params: {
        facilityId: 'ADS_53-F00007',
      },
      pathFromRoot: [],
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    const store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemUNAReviewDTO);
    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
  });

  it('should display controls', () => {
    expect(getByLabelText('Accepted')).toBeTruthy();
    expect(getByLabelText('Rejected')).toBeTruthy();
    expect(getByLabelText('Notes (optional)')).toBeTruthy();
    expect(getByLabelText('Upload evidence (optional)')).toBeTruthy();
  });

  it('should show checkbox and date input when accepted is clicked', async () => {
    click(getByLabelText('Accepted'));
    expect(document.getElementById('changeDate-0')).toBeTruthy();
    click(document.getElementById('changeDate-0') as HTMLElement);
    expect(document.getElementById('startDate')).toBeTruthy();
  });
});

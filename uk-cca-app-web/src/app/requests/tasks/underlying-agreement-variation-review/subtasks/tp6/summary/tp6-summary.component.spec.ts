import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '@requests/common';

import { mockVariationReviewRequestTaskState } from '../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { TP6SummaryComponent } from './tp6-summary.component';

describe('TP6SummaryComponent', () => {
  let store: RequestTaskStore;
  let fixture: ComponentFixture<TP6SummaryComponent>;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const setupComponent = (period: BaselineAndTargetPeriodsSubtasks) => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [TP6SummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: period },
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
      ],
    });

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockVariationReviewRequestTaskState);
    fixture = TestBed.createComponent(TP6SummaryComponent);
    fixture.detectChanges();
  };

  it('should match snapshot for TP6, ABSOLUTE agreementMeasurementType and no Measuremenet', () => {
    setupComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS);
    expect(fixture.nativeElement).toMatchSnapshot();
  });
});

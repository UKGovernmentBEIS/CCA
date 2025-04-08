import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { mockUNAReviewRequestTaskState } from '@requests/common';
import { render } from '@testing-library/angular';

import { mockVariationReviewRequestTaskState } from '../../../testing/variation-review-mock-data';
import { OverallDecisionSummaryComponent } from './overall-decision-summary.component';

describe('SummaryComponentUnaDetermination', () => {
  let store: RequestTaskStore;
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let container: Element;
  beforeEach(async () => {
    const result = await render(OverallDecisionSummaryComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setState(mockUNAReviewRequestTaskState);
      },
    });
    container = result.container;
  });
  it('should match overall decision summary', async () => {
    expect(container).toMatchSnapshot();
  });
});

describe('SummaryComponentVariationDetermination', () => {
  let store: RequestTaskStore;
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let container: Element;
  beforeEach(async () => {
    const result = await render(OverallDecisionSummaryComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setState(mockVariationReviewRequestTaskState);
      },
    });
    container = result.container;
  });
  it('should match overall decision summary', async () => {
    expect(container).toMatchSnapshot();
  });
});

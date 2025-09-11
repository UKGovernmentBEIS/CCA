import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { mockUNAReviewRequestTaskState } from '@requests/common';
import { render } from '@testing-library/angular';

import { OverallDecisionCheckYourAnswersComponent } from './overall-decision-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let store: RequestTaskStore;
  let container: Element;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    const result = await render(OverallDecisionCheckYourAnswersComponent, {
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

  it('should match snapshot for OverallDecisionCheckYourAnswersComponent', async () => {
    expect(container).toMatchSnapshot();
  });
});

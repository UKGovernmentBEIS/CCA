import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  mockUNAReviewRequestTaskState,
} from '@requests/common';
import { render } from '@testing-library/angular';

import { TP5CheckYourAnswersComponent } from './tp5-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let store: RequestTaskStore;
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let tree: Element;
  beforeEach(async () => {
    const renderResult = await render(TP5CheckYourAnswersComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS },
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
    tree = renderResult.container;
  });
  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});

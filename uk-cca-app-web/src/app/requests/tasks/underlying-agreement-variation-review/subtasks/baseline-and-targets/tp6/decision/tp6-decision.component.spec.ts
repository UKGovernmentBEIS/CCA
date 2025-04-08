import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '@requests/common';
import { render } from '@testing-library/angular';

import { mockVariationReviewRequestTaskState } from '../../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { TP6DecisionComponent } from './tp6-decision.component';

describe('TP6DecisionComponent', () => {
  let store: RequestTaskStore;
  let tree: Element;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    const renderResult = await render(TP6DecisionComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setState(mockVariationReviewRequestTaskState);
      },
    });

    tree = renderResult.container;
  });

  it('should match snapshot', () => {
    expect(tree).toMatchSnapshot();
  });
});

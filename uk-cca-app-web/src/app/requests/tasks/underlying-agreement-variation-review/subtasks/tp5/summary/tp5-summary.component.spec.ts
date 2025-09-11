import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '@requests/common';
import { render } from '@testing-library/angular';

import { mockVariationReviewRequestTaskState } from '../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { TP5SummaryComponent } from './tp5-summary.component';

describe('TP5SummaryComponent', () => {
  let store: RequestTaskStore;
  let container: Element;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  async function renderComponent(period: BaselineAndTargetPeriodsSubtasks) {
    const renderResult = await render(TP5SummaryComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: period },
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

    container = renderResult.container;
  }

  it('should match snapshot for TP5, ABSOLUTE agreementMeasurementType and no Measuremenet', async () => {
    await renderComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS);
    expect(container).toMatchSnapshot();
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '@requests/common';
import { render } from '@testing-library/angular';

import { mockRequestTaskState } from '../../../../testing/mock-data';
import { TP6SummaryComponent } from './tp6-summary.component';

describe('TP6SummaryComponent', () => {
  let store: RequestTaskStore;
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let container: Element;
  async function renderComponent(period: BaselineAndTargetPeriodsSubtasks) {
    const renderResult = await render(TP6SummaryComponent, {
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
        store.setState(mockRequestTaskState);
      },
    });
    container = renderResult.container;
  }
  it('should match snapshot for TP6, ABSOLUTE agreementMeasurementType and no Measuremenet', async () => {
    await renderComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS);
    expect(container).toMatchSnapshot();
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '@requests/common';
import { render } from '@testing-library/angular';

import { RequestTaskItemDTO } from 'cca-api';

import {
  mockRequestTaskItemDTO,
  mockRequestTaskItemDTOABSOLUTE,
  mockRequestTaskItemDTOABSOLUTENoMeasurement,
  mockRequestTaskItemDTONOVEM,
} from '../../../../testing/mock-data';
import { BaselineAndTargetsSummaryComponent } from './baseline-and-targets-summary.component';

describe('SummaryComponent', () => {
  let store: RequestTaskStore;
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let container: Element;
  async function renderComponent(period: BaselineAndTargetPeriodsSubtasks, taskItem: RequestTaskItemDTO) {
    const renderResult = await render(BaselineAndTargetsSummaryComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: period },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply for underlying agreement' },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(taskItem);
      },
    });
    container = renderResult.container;
  }
  it('should match snapshot for TP5', async () => {
    await renderComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS, mockRequestTaskItemDTO);
    expect(container).toMatchSnapshot();
  });
  it('should match snapshot for TP6 - RELATIVE agreementMeasurementType', async () => {
    await renderComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS, mockRequestTaskItemDTO);
    expect(container).toMatchSnapshot();
  });
  it('should match snapshot for TP6 and NOVEM agreementMeasurementType', async () => {
    await renderComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS, mockRequestTaskItemDTONOVEM);
    expect(container).toMatchSnapshot();
  });
  it('should match snapshot for TP6 and ABSOLUTE agreementMeasurementType', async () => {
    await renderComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS, mockRequestTaskItemDTOABSOLUTE);
    expect(container).toMatchSnapshot();
  });
  it('should match snapshot for TP6, ABSOLUTE agreementMeasurementType and no Measuremenet', async () => {
    await renderComponent(
      BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
      mockRequestTaskItemDTOABSOLUTENoMeasurement,
    );
    expect(container).toMatchSnapshot();
  });
});

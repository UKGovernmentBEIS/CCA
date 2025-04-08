import { DecimalPipe } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { render } from '@testing-library/angular';

import { mockRequestTaskItemDTOABSOLUTE, mockRequestTaskItemDTONOVEM } from '../../../testing';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '../../../underlying-agreement.types';
import { AddTargetsComponent } from './add-targets.component';

describe('AddTargetsComponent', () => {
  let container: Element;
  let store: RequestTaskStore;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  it('should correctly show data for ABSOLUTE/RELATIVE', async () => {
    const renderResult = await render(AddTargetsComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        DecimalPipe,
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(mockRequestTaskItemDTOABSOLUTE);
      },
    });

    container = renderResult.container;
  });

  it('should correctly show data for NOVEM', async () => {
    await render(AddTargetsComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        DecimalPipe,
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(mockRequestTaskItemDTONOVEM);
      },
    });

    expect(container).toMatchSnapshot();
  });
});

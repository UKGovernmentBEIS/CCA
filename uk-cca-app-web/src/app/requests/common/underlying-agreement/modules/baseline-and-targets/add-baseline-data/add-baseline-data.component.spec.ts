import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';
import produce from 'immer';

import {
  mockRequestTaskItemDTO,
  mockRequestTaskItemDTONOVEM,
  mockUnaRequestTaskPayload,
  mockUnaRequestTaskPayloadNOVEM,
} from '../../../testing';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '../../../underlying-agreement.types';
import { AddBaselineDataComponent } from './add-baseline-data.component';
describe('AddBaselineDataComponent non-NOVEM', () => {
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let store: RequestTaskStore;
  beforeEach(async () => {
    await render(AddBaselineDataComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(mockRequestTaskItemDTO);
        const payload = produce(mockUnaRequestTaskPayload, (p) => {
          delete p.underlyingAgreement.targetPeriod6Details.targets;
          delete p.underlyingAgreement.targetPeriod6Details.baselineData;
        });
        store.setPayload(payload);
      },
    });
  });
  it('should not render the form until the radio is clicked', () => {
    expect(document.getElementById('isTwelveMonths')).toBeVisible();
    expect(document.querySelector('#baselineDate')).toBeFalsy();
  });
  it('should correctly show errors for invalid yes fields', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByLabelText('Yes'));
    await user.click(screen.getByText('Continue'));
    expect(screen.getAllByText('Enter the start date of the baseline.')).toHaveLength(2);
    expect(screen.getAllByText('Enter the baseline kWh in the baseline period.')).toHaveLength(2);
    expect(
      screen.getAllByText('Enter the baseline energy to carbon conversion factor in the baseline period.'),
    ).toHaveLength(2);
    expect(screen.getAllByText('Enter the throughput in the baseline period.')).toHaveLength(2);
  });
  it('should correctly show errors for invalid no fields', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByLabelText('Yes'));
    await user.click(screen.getByText('Continue'));
    expect(screen.getAllByText('Enter the start date of the baseline.')).toHaveLength(2);
    expect(screen.getAllByText('Enter the baseline kWh in the baseline period.')).toHaveLength(2);
    expect(
      screen.getAllByText('Enter the baseline energy to carbon conversion factor in the baseline period.'),
    ).toHaveLength(2);
    expect(
      screen.getAllByText('Select yes if throughput was adjusted using the CHP special reporting mechanism.'),
    ).toHaveLength(2);
    expect(screen.getAllByText('Enter the throughput in the baseline period.')).toHaveLength(2);
  });
});

describe('AddBaselineDataComponent NOVEM', () => {
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  let store: RequestTaskStore;
  beforeEach(async () => {
    await render(AddBaselineDataComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(RequestTaskStore);
        store.setRequestTaskItem(mockRequestTaskItemDTONOVEM);
        const payload = produce(mockUnaRequestTaskPayloadNOVEM, (p) => {
          delete p.underlyingAgreement.targetPeriod6Details.targets;
          delete p.underlyingAgreement.targetPeriod6Details.baselineData;
        });
        store.setPayload(payload);
      },
    });
  });
  it('should not render the form until the radio is clicked', () => {
    expect(document.getElementById('isTwelveMonths')).toBeVisible();
    expect(document.querySelector('#baselineDate')).toBeFalsy();
  });
  it('should correctly show errors for invalid yes fields', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByLabelText('Yes'));
    await user.click(screen.getByText('Continue'));
    expect(screen.getAllByText('Enter the start date of the baseline.')).toHaveLength(2);
    expect(screen.getAllByText('Enter the baseline kWh in the baseline period.')).toHaveLength(2);
    expect(
      screen.getAllByText('Enter the baseline energy to carbon conversion factor in the baseline period.'),
    ).toHaveLength(2);
    expect(
      screen.queryAllByText('Select yes if throughput was adjusted using the CHP special reporting mechanism.'),
    ).toHaveLength(0);
    expect(screen.queryAllByAltText('Enter the throughput in the baseline period.')).toHaveLength(0);
  });
  it('should correctly show errors for invalid no fields', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByLabelText('Yes'));
    await user.click(screen.getByText('Continue'));
    expect(screen.getAllByText('Enter the start date of the baseline.')).toHaveLength(2);
    expect(screen.getAllByText('Enter the baseline kWh in the baseline period.')).toHaveLength(2);
    expect(
      screen.getAllByText('Enter the baseline energy to carbon conversion factor in the baseline period.'),
    ).toHaveLength(2);
    expect(
      screen.queryAllByText('Select yes if throughput was adjusted using the CHP special reporting mechanism.'),
    ).toHaveLength(0);
    expect(screen.queryAllByAltText('Enter the throughput in the baseline period.')).toHaveLength(0);
  });
});

import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { RequestTaskState, RequestTaskStore } from '@netz/common/store';
import {
  TaskItemStatus,
  TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
} from '@requests/common';
import { produce } from 'immer';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { mockTprRequestTaskState } from './testing/mock-data';
import { tprFormCanSubmitGuard } from './tpr-form-can-submit.guard';

@Component({ template: '' })
class EmptyComponent {}

const buildState = (sectionsCompleted: Record<string, string>): RequestTaskState => {
  const requestTask = mockTprRequestTaskState.requestTaskItem.requestTask;

  return {
    ...mockTprRequestTaskState,
    requestTaskItem: {
      ...mockTprRequestTaskState.requestTaskItem,
      requestTask: {
        ...requestTask,
        payload: {
          ...(requestTask.payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload),
          sectionsCompleted,
        } as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
      },
    },
  };
};

describe('tprFormCanSubmitGuard', () => {
  let router: Router;
  let store: RequestTaskStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        RequestTaskStore,
        provideRouter([
          {
            path: 'tasks/:taskId',
            children: [
              { path: '', component: EmptyComponent },
              {
                path: 'target-period-reporting-form',
                children: [
                  {
                    path: '',
                    children: [{ path: 'submit', canActivate: [tprFormCanSubmitGuard], component: EmptyComponent }],
                  },
                ],
              },
            ],
          },
        ]),
      ],
    });

    router = TestBed.inject(Router);
    store = TestBed.inject(RequestTaskStore);
  });

  it('should allow submit when both subtasks are completed', async () => {
    store.setState(
      buildState({
        [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
        [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
      }),
    );

    await router.navigateByUrl('/tasks/1/target-period-reporting-form/submit');

    expect(router.url).toBe('/tasks/1/target-period-reporting-form/submit');
  });

  it('should redirect to the task page when no subtask is completed', async () => {
    store.setState(buildState({}));

    await router.navigateByUrl('/tasks/1/target-period-reporting-form/submit');

    expect(router.url).toBe('/tasks/1');
  });

  it('should redirect to the task page when throughput details are in progress', async () => {
    store.setState(
      buildState({
        [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
        [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.IN_PROGRESS,
      }),
    );

    await router.navigateByUrl('/tasks/1/target-period-reporting-form/submit');

    expect(router.url).toBe('/tasks/1');
  });

  it('should redirect to the task page when both subtasks are completed but the throughput is not entered', async () => {
    const state = buildState({
      [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
      [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
    });
    const payload = state.requestTaskItem.requestTask
      .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestTask: {
          ...state.requestTaskItem.requestTask,
          payload: {
            ...payload,
            performanceData: {
              ...payload.performanceData,
              throughputDetails: { ...payload.performanceData.throughputDetails, actualThroughput: null },
            },
          } as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
        },
      },
    });

    await router.navigateByUrl('/tasks/1/target-period-reporting-form/submit');

    expect(router.url).toBe('/tasks/1');
  });

  const buildCompletedState = (mutate: (payload: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) => void) =>
    produce(
      buildState({
        [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
        [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
      }),
      (draft) =>
        mutate(draft.requestTaskItem.requestTask.payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload),
    );

  it('should redirect to the task page when both subtasks are completed but a non-standard fuel is incomplete', async () => {
    store.setState(
      buildCompletedState((payload) => {
        payload.performanceData.energyFuelDetails.nonStandardFuels = [
          { name: 'Biogas', conversionFactor: null, deliveredEnergy: '50', primaryEnergy: '50' },
        ];
      }),
    );

    await router.navigateByUrl('/tasks/1/target-period-reporting-form/submit');

    expect(router.url).toBe('/tasks/1');
  });

  it('should redirect to the task page when both subtasks are completed but zero energy is not allowed', async () => {
    store.setState(
      buildCompletedState((payload) => {
        payload.referenceData.baselineAndTargets.baselineVariableEnergy = '5000';
        payload.performanceData.energyFuelDetails.standardFuels = {};
        payload.performanceData.energyFuelDetails.nonStandardFuels = [];
      }),
    );

    await router.navigateByUrl('/tasks/1/target-period-reporting-form/submit');

    expect(router.url).toBe('/tasks/1');
  });
});

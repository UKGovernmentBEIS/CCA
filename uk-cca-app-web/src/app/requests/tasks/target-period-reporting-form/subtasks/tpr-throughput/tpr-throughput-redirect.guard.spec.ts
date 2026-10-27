import { TestBed } from '@angular/core/testing';
import { PRIMARY_OUTLET, UrlTree } from '@angular/router';

import { RequestTaskState, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteSnapshotStub } from '@netz/common/testing';
import {
  TaskItemStatus,
  TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
} from '@requests/common';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { mockTprRequestTaskStateThroughputTotalsOnly } from '../../testing/mock-data';
import { tprThroughputRedirectGuard } from './tpr-throughput-redirect.guard';

const basePayload = mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask
  .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;

const buildState = ({
  sectionsCompleted,
  baselineVariableEnergy = '5000',
  deliveredEnergy = '100',
}: {
  sectionsCompleted: Record<string, string>;
  baselineVariableEnergy?: string;
  deliveredEnergy?: string;
}): RequestTaskState =>
  ({
    ...mockTprRequestTaskStateThroughputTotalsOnly,
    requestTaskItem: {
      ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem,
      requestTask: {
        ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask,
        payload: {
          ...basePayload,
          referenceData: {
            ...basePayload.referenceData,
            baselineAndTargets: {
              ...basePayload.referenceData?.baselineAndTargets,
              baselineVariableEnergy,
            },
          },
          performanceData: {
            ...basePayload.performanceData,
            energyFuelDetails: {
              ...basePayload.performanceData?.energyFuelDetails,
              standardFuels: {
                GRID_ELECTRICITY: { deliveredEnergy, primaryEnergy: deliveredEnergy },
              },
              nonStandardFuels: [],
            },
          },
          sectionsCompleted,
        },
      },
    },
  }) as RequestTaskState;

describe('tprThroughputRedirectGuard', () => {
  let store: RequestTaskStore;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [RequestTaskStore] });
    store = TestBed.inject(RequestTaskStore);
  });

  const runGuard = () => {
    const route = new ActivatedRouteSnapshotStub();
    const result = TestBed.runInInjectionContext(() => tprThroughputRedirectGuard(route, {} as never)) as UrlTree;

    return result.root.children[PRIMARY_OUTLET]?.segments.map((segment) => segment.path) ?? [];
  };

  it('should redirect to summary when throughput details are completed', () => {
    store.setState(
      buildState({
        sectionsCompleted: {
          [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
          [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
        },
      }),
    );

    expect(runGuard()).toEqual(['summary']);
  });

  it('should redirect to check-your-answers when in progress and the energy data is valid', () => {
    store.setState(
      buildState({
        sectionsCompleted: {
          [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
          [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.IN_PROGRESS,
        },
      }),
    );

    expect(runGuard()).toEqual(['check-your-answers']);
  });

  it('should redirect to details when in progress but zero energy is not allowed', () => {
    store.setState(
      buildState({
        sectionsCompleted: {
          [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
          [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.IN_PROGRESS,
        },
        deliveredEnergy: '0',
      }),
    );

    expect(runGuard()).toEqual(['details']);
  });

  it('should redirect to check-your-answers when zero energy is allowed by the baseline', () => {
    store.setState(
      buildState({
        sectionsCompleted: {
          [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
          [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.IN_PROGRESS,
        },
        baselineVariableEnergy: '0',
        deliveredEnergy: '0',
      }),
    );

    expect(runGuard()).toEqual(['check-your-answers']);
  });

  it('should redirect to details when the subtask has not started', () => {
    store.setState(
      buildState({
        sectionsCompleted: {
          [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
        },
      }),
    );

    expect(runGuard()).toEqual(['details']);
  });
});

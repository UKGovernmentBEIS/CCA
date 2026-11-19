import { TestBed } from '@angular/core/testing';
import { PRIMARY_OUTLET, UrlTree } from '@angular/router';

import { RequestTaskState, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteSnapshotStub } from '@netz/common/testing';
import { TaskItemStatus, TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK } from '@requests/common';
import { produce } from 'immer';

import {
  PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
  PerformanceDataFacilityNonStandardFuel,
} from 'cca-api';

import { mockTprRequestTaskState } from '../../testing/mock-data';
import { energyFuelAmountRedirectGuard } from './energy-fuel-amount-redirect.guard';

const buildState = (
  sectionStatus: TaskItemStatus | undefined,
  nonStandardFuels: PerformanceDataFacilityNonStandardFuel[] = [],
): RequestTaskState =>
  produce(mockTprRequestTaskState, (draft) => {
    const payload = draft.requestTaskItem.requestTask
      .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
    payload.sectionsCompleted = sectionStatus ? { [TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK]: sectionStatus } : {};
    payload.performanceData.energyFuelDetails.nonStandardFuels = nonStandardFuels;
  });

describe('energyFuelAmountRedirectGuard', () => {
  let store: RequestTaskStore;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [RequestTaskStore] });
    store = TestBed.inject(RequestTaskStore);
  });

  const runGuard = () => {
    const route = new ActivatedRouteSnapshotStub();
    const result = TestBed.runInInjectionContext(() => energyFuelAmountRedirectGuard(route, {} as never)) as UrlTree;

    return result.root.children[PRIMARY_OUTLET]?.segments.map((segment) => segment.path) ?? [];
  };

  it('should redirect to summary when the subtask is completed', () => {
    store.setState(buildState(TaskItemStatus.COMPLETED));

    expect(runGuard()).toEqual(['summary']);
  });

  it('should redirect to details when completed but a non-standard fuel CO2 factor is cleared', () => {
    store.setState(
      buildState(TaskItemStatus.COMPLETED, [
        { name: 'Biogas', conversionFactor: null, deliveredEnergy: '50', primaryEnergy: '50' },
      ]),
    );

    expect(runGuard()).toEqual(['details']);
  });

  it('should redirect to check-your-answers when in progress and the non-standard fuels are complete', () => {
    store.setState(
      buildState(TaskItemStatus.IN_PROGRESS, [
        { name: 'Biogas', conversionFactor: '0.5', deliveredEnergy: '50', primaryEnergy: '50' },
      ]),
    );

    expect(runGuard()).toEqual(['check-your-answers']);
  });

  it('should redirect to details when in progress but a non-standard fuel CO2 factor is cleared', () => {
    store.setState(
      buildState(TaskItemStatus.IN_PROGRESS, [
        { name: 'Biogas', conversionFactor: null, deliveredEnergy: '50', primaryEnergy: '50' },
      ]),
    );

    expect(runGuard()).toEqual(['details']);
  });

  it('should redirect to details when in progress but a non-standard fuel delivered energy is 0', () => {
    store.setState(
      buildState(TaskItemStatus.IN_PROGRESS, [
        { name: 'Biogas', conversionFactor: '0.5', deliveredEnergy: '0', primaryEnergy: '0' },
      ]),
    );

    expect(runGuard()).toEqual(['details']);
  });

  it('should redirect to details when in progress but the 70% energy used answer is missing', () => {
    store.setState(
      produce(buildState(TaskItemStatus.IN_PROGRESS), (draft) => {
        const payload = draft.requestTaskItem.requestTask
          .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
        payload.performanceData.energyFuelDetails.atLeastSeventyPercentEnergyUsed = null;
      }),
    );

    expect(runGuard()).toEqual(['details']);
  });

  it('should redirect to check-your-answers when in progress and the 70% energy used answer is No', () => {
    store.setState(
      produce(buildState(TaskItemStatus.IN_PROGRESS), (draft) => {
        const payload = draft.requestTaskItem.requestTask
          .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
        payload.performanceData.energyFuelDetails.atLeastSeventyPercentEnergyUsed = false;
      }),
    );

    expect(runGuard()).toEqual(['check-your-answers']);
  });

  it('should redirect to details when in progress but SRM applies and the CHP value is missing', () => {
    store.setState(
      produce(buildState(TaskItemStatus.IN_PROGRESS), (draft) => {
        const payload = draft.requestTaskItem.requestTask
          .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
        payload.referenceData.baselineAndTargets.usedReportingMechanism = true;
        payload.performanceData.energyFuelDetails.electricitySuppliedFromCHP = null;
      }),
    );

    expect(runGuard()).toEqual(['details']);
  });

  it('should redirect to check-your-answers when in progress and SRM does not apply without a CHP value', () => {
    store.setState(
      produce(buildState(TaskItemStatus.IN_PROGRESS), (draft) => {
        const payload = draft.requestTaskItem.requestTask
          .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
        payload.referenceData.baselineAndTargets.usedReportingMechanism = false;
        payload.performanceData.energyFuelDetails.electricitySuppliedFromCHP = null;
      }),
    );

    expect(runGuard()).toEqual(['check-your-answers']);
  });

  it('should redirect to details when the subtask has not started', () => {
    store.setState(buildState(undefined));

    expect(runGuard()).toEqual(['details']);
  });
});

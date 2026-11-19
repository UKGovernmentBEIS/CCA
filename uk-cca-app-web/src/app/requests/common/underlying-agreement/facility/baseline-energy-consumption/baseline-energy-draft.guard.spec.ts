import { TestBed } from '@angular/core/testing';

import { RequestTaskState, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteSnapshotStub } from '@netz/common/testing';

import { FacilityBaselineEnergyConsumption } from 'cca-api';

import { initializeBaselineEnergyDraftGuard } from './baseline-energy-draft.guard';
import { BaselineEnergyDraftService } from './baseline-energy-draft.service';

const baselineEnergyConsumption = {
  totalFixedEnergy: '150',
  hasVariableEnergy: true,
  variableEnergyType: 'BY_PRODUCT',
  variableEnergyConsumptionDataByProduct: [{ productName: 'prod1', productStatus: 'LIVE' }],
} as FacilityBaselineEnergyConsumption;

const state = {
  requestTaskItem: {
    requestTask: {
      payload: {
        underlyingAgreement: {
          facilities: [
            { facilityId: 'ADS_1-F00001' },
            {
              facilityId: 'ADS_1-F00002',
              cca3BaselineAndTargets: { facilityBaselineEnergyConsumption: baselineEnergyConsumption },
            },
          ],
        },
      },
    },
  },
} as unknown as RequestTaskState;

describe('initializeBaselineEnergyDraftGuard', () => {
  let draftService: BaselineEnergyDraftService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [RequestTaskStore, BaselineEnergyDraftService] });
    TestBed.inject(RequestTaskStore).setState(state);
    draftService = TestBed.inject(BaselineEnergyDraftService);
  });

  const runGuard = (facilityId: string) =>
    TestBed.runInInjectionContext(() =>
      initializeBaselineEnergyDraftGuard(new ActivatedRouteSnapshotStub({ facilityId }), {} as never),
    );

  it('should seed the draft from the facility in the store', () => {
    expect(runGuard('ADS_1-F00002')).toBe(true);

    expect(draftService.draftSignal()).toEqual({
      totalFixedEnergy: '150',
      hasVariableEnergy: true,
      variableEnergyType: 'BY_PRODUCT',
      products: [{ productName: 'prod1', productStatus: 'LIVE' }],
    });
  });

  it('should let a product page mutate the draft without visiting the parent page first', () => {
    runGuard('ADS_1-F00002');

    expect(() => draftService.removeProduct('prod1')).not.toThrow();
    expect(draftService.draftSignal().products).toEqual([]);
  });

  it('should not throw on a product change when the facility has no saved products', () => {
    runGuard('ADS_1-F00001');

    expect(() => draftService.removeProduct('prod1')).not.toThrow();
    expect(() => draftService.excludeProduct('prod1')).not.toThrow();
    expect(() => draftService.undoExcludeProduct('prod1')).not.toThrow();
  });

  it('should reset a draft left from a previous visit to the store values', () => {
    runGuard('ADS_1-F00002');
    draftService.removeProduct('prod1');

    runGuard('ADS_1-F00002');

    expect(draftService.draftSignal().products).toEqual([{ productName: 'prod1', productStatus: 'LIVE' }]);
  });
});

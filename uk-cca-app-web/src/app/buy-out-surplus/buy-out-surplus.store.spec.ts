import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { Mocked } from 'vitest';

import { RequestsService } from 'cca-api';

import { BUY_OUT_SURPLUS_REQUEST_TYPES, BuyoutSurplusStore } from './buy-out-surplus.store';
import { buyoutSurplusStateMockData } from './testing/mock-data';

describe('BuyoutSurplusStore', () => {
  let store: BuyoutSurplusStore;
  let requestsService: Partial<Mocked<RequestsService>>;

  beforeEach(() => {
    requestsService = {
      getRequestDetailsByResource: vi.fn().mockReturnValue(
        of({
          requestDetails: buyoutSurplusStateMockData.workflowsHistory,
          total: buyoutSurplusStateMockData.totalWorkflowHistoryItems,
        }),
      ),
    };

    TestBed.configureTestingModule({
      providers: [BuyoutSurplusStore, { provide: RequestsService, useValue: requestsService }],
    });

    store = TestBed.inject(BuyoutSurplusStore);
  });

  it('should treat a run of either scheme as blocking', () => {
    expect(BUY_OUT_SURPLUS_REQUEST_TYPES).toEqual(['BUY_OUT_SURPLUS_RUN', 'BUY_OUT_SURPLUS_FACILITY_RUN']);
  });

  it('should poll both schemes when looking for a batch run in progress', () => {
    store.checkForPendingBatchRun().subscribe();

    expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTypes: BUY_OUT_SURPLUS_REQUEST_TYPES,
        requestStatuses: ['IN_PROGRESS'],
      }),
    );
  });

  it('should report a CCA3 run in progress as blocking', async () => {
    requestsService.getRequestDetailsByResource.mockReturnValue(
      of({
        requestDetails: [{ requestType: 'BUY_OUT_SURPLUS_FACILITY_RUN', requestStatus: 'IN_PROGRESS' }],
        total: 1,
      }),
    );

    await expect(new Promise((resolve) => store.checkForPendingBatchRun().subscribe(resolve))).resolves.toBe(true);
  });

  it('should list both schemes in the workflow history', () => {
    store.fetchAndSetWorkflows();

    expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledWith(
      expect.objectContaining({ requestTypes: BUY_OUT_SURPLUS_REQUEST_TYPES }),
    );
  });
});

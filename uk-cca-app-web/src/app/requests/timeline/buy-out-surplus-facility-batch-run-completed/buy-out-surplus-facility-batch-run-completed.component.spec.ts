import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionState, RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

// import { BuyOutSurplusFacilityRunCompletedRequestActionPayload } from 'cca-api';
import { BuyOutSurplusFacilityBatchRunCompletedComponent } from './buy-out-surplus-facility-batch-run-completed.component';
import {
  buyOutSurplusFacilityBatchRunCompletedActionStateMock,
  buyOutSurplusFacilityBatchRunCompletedMock,
  buyOutSurplusFacilityBatchRunCompletedRequestActionDTO,
  buyOutSurplusFacilityBatchRunCompletedWithFailuresActionStateMock,
} from './tests/mock-data';

describe('BuyOutSurplusFacilityBatchRunCompletedComponent', () => {
  let component: BuyOutSurplusFacilityBatchRunCompletedComponent;
  let fixture: ComponentFixture<BuyOutSurplusFacilityBatchRunCompletedComponent>;

  const targetPeriodSections = [
    [
      [
        'Cost (GBP / tCO2e)',
        'Total facilities',
        'Total buy-out transactions',
        'Total buy-out facilities',
        'Total refund transactions',
        'Total refund facilities',
      ],
      ['38', '5,500', '1,700', '4,000', '2', '2'],
    ],
    [
      [
        'Cost (GBP / tCO2e)',
        'Total facilities',
        'Total buy-out transactions',
        'Total buy-out facilities',
        'Total refund transactions',
        'Total refund facilities',
      ],
      ['37', '5,500', '1,700', '4,000', '2', '2'],
    ],
  ];

  const createComponent = async (state: RequestActionState) => {
    await TestBed.configureTestingModule({
      imports: [BuyOutSurplusFacilityBatchRunCompletedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    TestBed.inject(RequestActionStore).setState(state);

    fixture = TestBed.createComponent(BuyOutSurplusFacilityBatchRunCompletedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  describe('when the run is completed', () => {
    beforeEach(async () => {
      await createComponent(buyOutSurplusFacilityBatchRunCompletedActionStateMock);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the correct data', () => {
      expect(getSummaryListData(fixture.nativeElement)).toEqual([
        [
          ['Run ID', 'Status', 'Batch run summary report', 'Total target units'],
          [
            'BOS-TP8001',
            'Completed',
            'BOS-TP8001 buy-out and surplus summary report.csv (opens in a new tab)',
            '3,000',
          ],
        ],
        ...targetPeriodSections,
      ]);
    });
  });

  describe('when the action changes in the store', () => {
    beforeEach(async () => {
      await createComponent(buyOutSurplusFacilityBatchRunCompletedActionStateMock);
    });

    it('should recompute the summary from the current action', () => {
      // BuyOutSurplusFacilityRunCompletedRequestActionPayload
      const payload = {
        ...buyOutSurplusFacilityBatchRunCompletedMock,
        runSummary: {
          totalAccounts: 10,
          failedAccounts: 4,
          totalFacilities: 20,
          failedFacilities: 6,
          runSummaryEntries: [],
        } as any,
        csvFile: { name: 'BOS-TP7002 buy-out and surplus summary report.csv', uuid: 'uuid' },
      };

      TestBed.inject(RequestActionStore).setState({
        action: {
          ...buyOutSurplusFacilityBatchRunCompletedRequestActionDTO,
          type: 'BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_WITH_FAILURES',
          requestId: 'BOS-TP7002',
          payload,
        },
      });
      fixture.detectChanges();

      expect(getSummaryListData(fixture.nativeElement)).toEqual([
        [
          [
            'Run ID',
            'Status',
            'Batch run summary report',
            'Total target units',
            'Failed target units',
            'Failed facilities',
          ],
          [
            'BOS-TP7002',
            'Completed with failures',
            'BOS-TP7002 buy-out and surplus summary report.csv (opens in a new tab)',
            '10',
            '4',
            '6',
          ],
        ],
      ]);
    });
  });

  describe('when the run is completed with failures', () => {
    beforeEach(async () => {
      await createComponent(buyOutSurplusFacilityBatchRunCompletedWithFailuresActionStateMock);
    });

    it('should display the correct data', () => {
      expect(getSummaryListData(fixture.nativeElement)).toEqual([
        [
          [
            'Run ID',
            'Status',
            'Batch run summary report',
            'Total target units',
            'Failed target units',
            'Failed facilities',
          ],
          [
            'BOS-TP8001',
            'Completed with failures',
            'BOS-TP8001 buy-out and surplus summary report.csv (opens in a new tab)',
            '3,000',
            '1,700',
            '3,500',
          ],
        ],
        ...targetPeriodSections,
      ]);
    });
  });
});

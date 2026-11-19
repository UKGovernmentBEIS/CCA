import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { BuyOutSurplusFacilityRunRequestMetadata } from 'cca-api';

import { BuyoutSurplusStore } from '../buy-out-surplus.store';
import { buyoutSurplusStateMockData } from '../testing/mock-data';
import { WorkflowHistoryTabComponent } from './workflow-history-tab.component';

describe('WorkflowHistoryTabComponent', () => {
  let component: WorkflowHistoryTabComponent;
  let fixture: ComponentFixture<WorkflowHistoryTabComponent>;
  let buyoutSurplusStore: BuyoutSurplusStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkflowHistoryTabComponent],
      providers: [
        BuyoutSurplusStore,
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    buyoutSurplusStore = TestBed.inject(BuyoutSurplusStore);
    buyoutSurplusStore.setState(buyoutSurplusStateMockData);

    fixture = TestBed.createComponent(WorkflowHistoryTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate message when no data available', () => {
    buyoutSurplusStore.setState({
      ...buyoutSurplusStore.state,
      totalWorkflowHistoryItems: 0,
      workflowsHistory: [],
    });

    fixture.detectChanges();

    expect(getByText('There are no workflow history events yet.')).toBeTruthy();
    expect(
      getByText('More information will be available when you create a new buy-out and surplus batch.'),
    ).toBeTruthy();
  });

  it('displays supplied facility counts, preserves zeros and leaves unavailable counts blank', () => {
    const metadata: BuyOutSurplusFacilityRunRequestMetadata = {
      type: 'BUY_OUT_SURPLUS_FACILITY_RUN',
      targetPeriodType: 'TP7',
      totalFacilities: 1234,
      totalAccounts: 20,
      failedFacilities: 12,
      failedAccounts: 2,
    };
    const completedRun = {
      id: 'BOS-TP7001',
      requestType: 'BUY_OUT_SURPLUS_FACILITY_RUN',
      requestStatus: 'COMPLETED',
      creationDate: '2026-10-09',
      requestMetadata: metadata,
    };
    const workflowsHistory = [
      ...buyoutSurplusStateMockData.workflowsHistory,
      completedRun,
      {
        ...completedRun,
        id: 'BOS-TP7002',
        requestMetadata: { ...metadata, totalFacilities: 0, failedFacilities: 0 },
      },
      { ...completedRun, id: 'BOS-TP7003', requestStatus: 'IN_PROGRESS', requestMetadata: undefined },
    ];
    buyoutSurplusStore.updateState({ workflowsHistory, totalWorkflowHistoryItems: workflowsHistory.length });
    fixture.detectChanges();

    const root: HTMLElement = fixture.nativeElement;
    expect(Array.from(root.querySelectorAll('thead th')).map((cell) => cell.textContent.trim())).toEqual([
      'Run ID',
      'Date initiated',
      'Process status',
      'Total facilities',
      'Total TUs',
      'Failed facilities',
      'Failed TUs',
    ]);
    const rows = Array.from(root.querySelectorAll('tbody tr'));
    expect(rows).toHaveLength(workflowsHistory.length);
    expect(
      rows.map((row) =>
        Array.from(row.querySelectorAll('td'))
          .slice(3)
          .map((cell) => cell.textContent.trim()),
      ),
    ).toEqual([
      ['', '2', '', '1'],
      ['', '2', '', '1'],
      ['1,234', '20', '12', '2'],
      ['0', '20', '0', '2'],
      ['', '', '', ''],
    ]);
    expect(rows[2].querySelector('a').getAttribute('href')).toBe('/buyout-surplus/workflow-history/BOS-TP7001');
    for (const index of [4, 5, 6, 7]) {
      expect(root.querySelector(`thead th:nth-child(${index})`).classList).toContain('govuk-table__header--numeric');
      for (const row of rows) {
        expect(row.querySelector(`td:nth-child(${index})`).classList).toContain('govuk-table__cell--numeric');
      }
    }
  });
});

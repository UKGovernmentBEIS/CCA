import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

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
        provideHttpClient(),
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

  it('should populate with correct data', () => {
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(
      buyoutSurplusStateMockData.workflowsHistory.length + 1,
    );
  });
});

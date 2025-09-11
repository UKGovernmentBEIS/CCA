import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SubsistenceFeesStore } from '../subsistence-fees.store';
import { subsistenceFeesStateMockData } from '../testing/mock-data';
import { WorkflowHistoryTabComponent } from './workflow-history-tab.component';

describe('WorkflowHistoryTabComponent', () => {
  let component: WorkflowHistoryTabComponent;
  let fixture: ComponentFixture<WorkflowHistoryTabComponent>;
  let subsistenceFeesStore: SubsistenceFeesStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkflowHistoryTabComponent],
      providers: [
        SubsistenceFeesStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    subsistenceFeesStore = TestBed.inject(SubsistenceFeesStore);
    subsistenceFeesStore.setState(subsistenceFeesStateMockData);

    fixture = TestBed.createComponent(WorkflowHistoryTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate message when no data available', () => {
    subsistenceFeesStore.setState({
      ...subsistenceFeesStore.state,
      totalWorkflowHistoryItems: 0,
      workflowsHistory: [],
    });

    fixture.detectChanges();

    expect(screen.getByText('There are no workflow history events yet.')).toBeInTheDocument();
    expect(
      screen.getByText('More information will be available when you create a new payment request.'),
    ).toBeInTheDocument();
  });

  it('should populate with correct data', () => {
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(
      subsistenceFeesStateMockData.workflowsHistory.length + 1,
    );
  });
});

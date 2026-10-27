import { provideHttpClient, withXhr } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, getByText } from '@testing';

import { mockCca3SubmittedRequestAction, mockRequestActions, mockWorkflowDetails } from './testing/mock-data';
import { WorkflowHistoryComponent } from './workflow-history.component';

describe('WorkflowHistoryComponent', () => {
  let component: WorkflowHistoryComponent;
  let fixture: ComponentFixture<WorkflowHistoryComponent>;
  let route: ActivatedRouteStub;

  beforeEach(async () => {
    route = new ActivatedRouteStub(null, null, {
      details: {
        workflowDetails: mockWorkflowDetails,
        requestActions: mockRequestActions,
      },
    });

    await TestBed.configureTestingModule({
      imports: [WorkflowHistoryComponent],
      providers: [
        provideHttpClient(withXhr()),
        {
          provide: ActivatedRoute,
          useValue: route,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkflowHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render workflow details heading', () => {
    expect(getByTestId('page-heading').textContent).toContain('BOS-TP6003');
    expect(getByText('Completed')).toBeTruthy();
  });

  it('should render timeline events', () => {
    expect(document.querySelectorAll('[data-testid="timeline-item"]')).toHaveLength(2);
  });

  it('should render the CCA3 batch run submitted event as text without a link', () => {
    fixture.destroy();
    route.snapshot.data.details.requestActions = [mockCca3SubmittedRequestAction];

    fixture = TestBed.createComponent(WorkflowHistoryComponent);
    fixture.detectChanges();

    const timelineItem = getByTestId('timeline-item');
    expect(timelineItem.textContent).toContain('Buy-out and surplus batch run submitted by Regulator England');
    expect(timelineItem.querySelector('a')).toBeNull();
  });
});

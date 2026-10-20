import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, getByText } from '@testing';

import { mockRequestActions, mockWorkflowDetails } from './testing/mock-data';
import { WorkflowHistoryComponent } from './workflow-history.component';

describe('WorkflowHistoryComponent', () => {
  let component: WorkflowHistoryComponent;
  let fixture: ComponentFixture<WorkflowHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkflowHistoryComponent],
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            details: {
              workflowDetails: mockWorkflowDetails,
              requestActions: mockRequestActions,
            },
          }),
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
});

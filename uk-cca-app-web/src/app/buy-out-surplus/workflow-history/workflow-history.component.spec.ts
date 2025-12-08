import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

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
    expect(screen.getByTestId('page-heading')).toHaveTextContent('BOS-TP6003');
    expect(screen.getByText('Completed')).toBeVisible();
  });

  it('should render timeline events', () => {
    expect(screen.getAllByTestId('timeline-item')).toHaveLength(2);
  });
});

import { provideHttpClient } from '@angular/common/http';
import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, getByText } from '@testing';

import { mockEmptyRequestItems, mockRequestActions, mockWorkflowDetails } from './testing/mock-data';
import { WorkflowDetailsComponent } from './workflow-details.component';

describe('WorkflowDetailsComponent', () => {
  let fixture: ComponentFixture<WorkflowDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkflowDetailsComponent],
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            workflowDetailsItemsAndActions: {
              workflowDetails: mockWorkflowDetails,
              requestItems: mockEmptyRequestItems,
              requestActions: mockRequestActions,
            },
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkflowDetailsComponent);
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should render workflow details heading', () => {
    expect(
      ((getByTestId('page-heading') as HTMLElement | null)?.textContent ?? '').replace(/\s+/g, ' ').trim(),
    ).toContain('ADS_1-S2513 Subsistence fees Completed');
    expect(getByText('Completed')).toBeTruthy();
  });

  it('should render timeline events', () => {
    expect(getByTestId('timeline-item')).toBeTruthy();
  });
});

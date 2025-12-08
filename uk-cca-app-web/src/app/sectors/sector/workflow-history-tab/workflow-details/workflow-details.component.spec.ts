import { provideHttpClient } from '@angular/common/http';
import { waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render, screen } from '@testing-library/angular';

import { mockEmptyRequestItems, mockRequestActions, mockWorkflowDetails } from './testing/mock-data';
import { WorkflowDetailsComponent } from './workflow-details.component';

describe('WorkflowDetailsComponent', () => {
  beforeEach(async () => {
    const { fixture } = await render(WorkflowDetailsComponent, {
      providers: [provideHttpClient()],
      componentProviders: [
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
    });

    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should render workflow details heading', waitForAsync(async () => {
    expect(screen.getByTestId('page-heading')).toHaveTextContent('ADS_1-S2513 Subsistence fees Completed');
    expect(screen.getByText('Completed')).toBeVisible();
  }));

  it('should render timeline events', waitForAsync(async () => {
    expect(screen.getByTestId('timeline-item')).toBeVisible();
  }));
});

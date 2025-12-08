import { provideHttpClient } from '@angular/common/http';
import { waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render, screen } from '@testing-library/angular';

import { mockEmptyRequestItems, mockRequestActions, mockRequestItems, mockWorkflowDetails } from './testing/mock-data';
import { WorkflowDetailsComponent } from './workflow-details.component';

describe('WorkflowDetailsComponent', () => {
  describe('with timeline events', () => {
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
      expect(screen.getByTestId('page-heading')).toHaveTextContent('ACC-ADS_2T00002 Account creation Completed');
      expect(screen.getByText('Completed')).toBeVisible();
    }));

    it('should render timeline events', waitForAsync(async () => {
      expect(screen.getByTestId('timeline-item')).toBeVisible();
    }));
  });

  describe('without timeline events and with request items', () => {
    beforeEach(async () => {
      const { fixture } = await render(WorkflowDetailsComponent, {
        providers: [provideHttpClient()],
        componentProviders: [
          {
            provide: ActivatedRoute,
            useValue: new ActivatedRouteStub(null, null, {
              workflowDetailsItemsAndActions: {
                workflowDetails: mockWorkflowDetails,
                requestItems: mockRequestItems,
                requestActions: [],
              },
            }),
          },
        ],
      });

      fixture.detectChanges();
      await fixture.whenStable();
    });

    it('should render tasks to complete when requestItems are not empty', waitForAsync(async () => {
      expect(screen.getByTestId('related-tasks')).toBeVisible();
    }));
  });
});

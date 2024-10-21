import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { render, screen } from '@testing-library/angular';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { mockRequestTaskItem, mockWorkflowTaskHeaderInfo } from './testing/mock-data';
import { WorkflowTaskHeaderComponent } from './workflow-task-header.component';

describe('WorkflowTaskHeaderComponent', () => {
  describe('with requestTaskStore requestInfo data inside a workflow', () => {
    let requestTaskStore;
    let targetUnitAccountInfoViewService: jest.Mocked<Partial<TargetUnitAccountInfoViewService>>;

    beforeEach(async () => {
      const { fixture } = await render(WorkflowTaskHeaderComponent, {
        providers: [provideHttpClient(), provideHttpClientTesting()],
        configureTestBed: (testbed) => {
          targetUnitAccountInfoViewService = {
            getAccountHeaderInfoById: jest.fn().mockReturnValue(of(mockWorkflowTaskHeaderInfo)),
          };
          testbed.overrideProvider(TargetUnitAccountInfoViewService, {
            useValue: targetUnitAccountInfoViewService,
          });
        },
      });

      requestTaskStore = TestBed.inject(RequestTaskStore);
      requestTaskStore.setRequestTaskItem(mockRequestTaskItem);
      fixture.detectChanges();
      await fixture.whenStable();
    });

    it('should send an api call to getAccountHeaderInfoById and render header elements in the DOM', async () => {
      expect(targetUnitAccountInfoViewService.getAccountHeaderInfoById).toHaveBeenCalledTimes(1);
      const taskHeaderName = screen.getByTestId('name');
      const taskHeaderBusinessId = screen.getByTestId('businessId');
      const taskHeaderStatus = screen.getByTestId('status');
      expect(taskHeaderName).toBeInTheDocument();
      expect(taskHeaderBusinessId).toBeInTheDocument();
      expect(taskHeaderStatus).toBeInTheDocument();
      expect(taskHeaderName).toHaveTextContent('Target Unit Account 6');
      expect(taskHeaderBusinessId).toHaveTextContent('ADS_2-T00008');
      expect(taskHeaderStatus).toHaveTextContent('Status: LIVE');
    });
  });

  describe('without requestTaskStore requestInfo data ', () => {
    let requestTaskStore;
    let targetUnitAccountInfoViewService: jest.Mocked<Partial<TargetUnitAccountInfoViewService>>;

    beforeEach(async () => {
      const { fixture } = await render(WorkflowTaskHeaderComponent, {
        providers: [provideHttpClient(), provideHttpClientTesting()],
        configureTestBed: (testbed) => {
          targetUnitAccountInfoViewService = {
            getAccountHeaderInfoById: jest.fn().mockReturnValue(of(null)),
          };
          testbed.overrideProvider(TargetUnitAccountInfoViewService, {
            useValue: targetUnitAccountInfoViewService,
          });
        },
      });

      requestTaskStore = TestBed.inject(RequestTaskStore);
      requestTaskStore.setRequestTaskItem({});
      fixture.detectChanges();
      await fixture.whenStable();
    });

    it('should render nothing when the request task store has no value for requestInfo', async () => {
      requestTaskStore.setRequestTaskItem({});
      expect(targetUnitAccountInfoViewService.getAccountHeaderInfoById).toHaveBeenCalledTimes(0);
      expect(screen.queryByRole('div')).toBeNull();
    });
  });
});

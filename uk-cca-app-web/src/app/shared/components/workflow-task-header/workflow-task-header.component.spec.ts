import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { type ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { getByTestId, queryByTestId } from '@testing';

import { TasksService } from 'cca-api';

import { mockRequestTaskItem, mockWorkflowTaskHeaderInfo } from './testing/mock-data';
import { WorkflowTaskHeaderComponent } from './workflow-task-header.component';

describe('WorkflowTaskHeaderComponent', () => {
  describe('with requestTaskStore requestInfo data inside a workflow', () => {
    let fixture: ComponentFixture<WorkflowTaskHeaderComponent>;
    let requestTaskStore: RequestTaskStore;
    let tasksService: jest.Mocked<Partial<TasksService>>;

    beforeEach(async () => {
      tasksService = {
        getRequestTaskHeaderInfo: jest.fn().mockReturnValue(of(mockWorkflowTaskHeaderInfo)),
      };

      await TestBed.configureTestingModule({
        imports: [WorkflowTaskHeaderComponent],
        providers: [
          provideHttpClient(),
          provideHttpClientTesting(),
          {
            provide: TasksService,
            useValue: tasksService,
          },
        ],
      }).compileComponents();

      fixture = TestBed.createComponent(WorkflowTaskHeaderComponent);

      requestTaskStore = TestBed.inject(RequestTaskStore);
      requestTaskStore.setRequestTaskItem(mockRequestTaskItem);
      await fixture.whenStable();
      fixture.detectChanges();
    });

    it('should send an api call to getRequestTaskHeaderInfo and render header elements in the DOM', () => {
      expect(tasksService.getRequestTaskHeaderInfo).toHaveBeenCalledTimes(1);
      const taskHeaderName = getByTestId('name');
      const taskHeaderBusinessId = getByTestId('businessId');
      const taskHeaderStatus = getByTestId('status');

      expect(taskHeaderName).toBeTruthy();
      expect(taskHeaderBusinessId).toBeTruthy();
      expect(taskHeaderStatus).toBeTruthy();
      expect((taskHeaderName as HTMLElement | null)?.textContent ?? '').toContain('Target Unit Account 6');
      expect((taskHeaderBusinessId as HTMLElement | null)?.textContent ?? '').toContain('ADS_2-T00008');
      expect((taskHeaderStatus as HTMLElement | null)?.textContent ?? '').toContain('Status: LIVE');
    });
  });

  describe('without requestTaskStore requestInfo data ', () => {
    let fixture: ComponentFixture<WorkflowTaskHeaderComponent>;
    let requestTaskStore: RequestTaskStore;
    let tasksService: jest.Mocked<Partial<TasksService>>;

    beforeEach(async () => {
      tasksService = {
        getRequestTaskHeaderInfo: jest.fn().mockReturnValue(of(null)),
      };

      await TestBed.configureTestingModule({
        imports: [WorkflowTaskHeaderComponent],
        providers: [
          provideHttpClient(),
          provideHttpClientTesting(),
          {
            provide: TasksService,
            useValue: tasksService,
          },
        ],
      }).compileComponents();

      fixture = TestBed.createComponent(WorkflowTaskHeaderComponent);

      requestTaskStore = TestBed.inject(RequestTaskStore);
      requestTaskStore.setRequestTaskItem({});
      await fixture.whenStable();

      fixture.detectChanges();
    });

    it('should render nothing when the request task store has no value for requestInfo', () => {
      requestTaskStore.setRequestTaskItem({});
      expect(tasksService.getRequestTaskHeaderInfo).toHaveBeenCalledTimes(0);
      expect(queryByTestId('name')).toBeNull();
      expect(queryByTestId('businessId')).toBeNull();
      expect(queryByTestId('status')).toBeNull();
    });
  });
});

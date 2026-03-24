import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { BASELINE_AND_TARGETS_SUBTASK, BaselineAndTargetPeriodsSubtasks } from '@requests/common';

import { RequestTaskItemDTO } from 'cca-api';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import { BaselineAndTargetsSummaryComponent } from './baseline-and-targets-summary.component';

describe('SummaryComponent', () => {
  let fixture: ComponentFixture<BaselineAndTargetsSummaryComponent>;
  let store: RequestTaskStore;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  async function setupComponent(period: BaselineAndTargetPeriodsSubtasks, taskItem: RequestTaskItemDTO) {
    await TestBed.configureTestingModule({
      imports: [BaselineAndTargetsSummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ taskId: '1' }) },
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: period },
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Vary the underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(taskItem);
    fixture = TestBed.createComponent(BaselineAndTargetsSummaryComponent);
    fixture.detectChanges();
  }

  it('should match snapshot for TP5', async () => {
    await setupComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS, mockRequestTaskItemDTO);
    expect(fixture.nativeElement).toMatchSnapshot();
  });

  it('should match snapshot for TP6 - RELATIVE agreementMeasurementType', async () => {
    await setupComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS, mockRequestTaskItemDTO);
    expect(fixture.nativeElement).toMatchSnapshot();
  });
});

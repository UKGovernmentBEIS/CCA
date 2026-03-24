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

describe('BaselineAndTargetsSummaryComponent', () => {
  let store: RequestTaskStore;
  let fixture: ComponentFixture<BaselineAndTargetsSummaryComponent>;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const setupComponent = (period: BaselineAndTargetPeriodsSubtasks, taskItem: RequestTaskItemDTO) => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [BaselineAndTargetsSummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: BASELINE_AND_TARGETS_SUBTASK, useValue: period },
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: unaTaskService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply to vary the underlying agreement' },
      ],
    });

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(taskItem);
    fixture = TestBed.createComponent(BaselineAndTargetsSummaryComponent);
    fixture.detectChanges();
  };

  it('should match snapshot for TP5', () => {
    setupComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS, mockRequestTaskItemDTO);
    expect(fixture.nativeElement).toMatchSnapshot();
  });

  it('should match snapshot for TP6 - RELATIVE agreementMeasurementType', () => {
    setupComponent(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS, mockRequestTaskItemDTO);
    expect(fixture.nativeElement).toMatchSnapshot();
  });
});

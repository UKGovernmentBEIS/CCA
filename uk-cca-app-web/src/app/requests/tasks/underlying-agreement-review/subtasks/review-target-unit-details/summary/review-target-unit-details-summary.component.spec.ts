import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockUNAReviewRequestTaskState } from '@requests/common';

import ReviewTargetUnitDetailsSummaryComponent from './review-target-unit-details-summary.component';

describe('Review Target Unit Details Summary', () => {
  let fixture: ComponentFixture<ReviewTargetUnitDetailsSummaryComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub();
  const taskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewTargetUnitDetailsSummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TaskService, useValue: taskService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);

    fixture = TestBed.createComponent(ReviewTargetUnitDetailsSummaryComponent);
    fixture.detectChanges();
  });

  it('should render the summary sections and rows', () => {
    expect(fixture).toMatchSnapshot();
  });
});

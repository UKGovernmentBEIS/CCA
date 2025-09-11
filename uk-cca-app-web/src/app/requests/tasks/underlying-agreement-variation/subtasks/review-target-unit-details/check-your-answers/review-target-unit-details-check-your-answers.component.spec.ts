import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskState } from '../../../testing/mock-data';
import ReviewTargetUnitDetailsCheckYourAnswersComponent from './review-target-unit-details-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: ReviewTargetUnitDetailsCheckYourAnswersComponent;
  let fixture: ComponentFixture<ReviewTargetUnitDetailsCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub();

  const mockTasksApiService: Partial<jest.Mocked<TasksApiService>> = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const submitSubtaskSpy = jest.spyOn(mockTasksApiService, 'saveRequestTaskAction');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewTargetUnitDetailsCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply to vary the underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(ReviewTargetUnitDetailsCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = screen.getByRole('heading', { name: 'Check your answers' });
    expect(heading).toBeInTheDocument();
  });

  it('should render the summary sections and rows', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should submit form and call submitSubtask method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and complete'));
    expect(submitSubtaskSpy).toHaveBeenCalledTimes(1);
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { REVIEW_TARGET_UNIT_DETAILS_SUBTASK, TasksApiService } from '@requests/common';
import { screen, waitFor } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskState } from '../../../testing/mock-data';
import { ReviewTargetUnitDetailsCheckYourAnswersComponent } from './review-target-unit-details-check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: ReviewTargetUnitDetailsCheckYourAnswersComponent;
  let fixture: ComponentFixture<ReviewTargetUnitDetailsCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub();

  const tasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const saveRequestTaskActionSpy = jest.spyOn(tasksApiService, 'saveRequestTaskAction');

  const enhancedMockState = {
    ...mockRequestTaskState,
    payload: {
      underlyingAgreement: {
        underlyingAgreementTargetUnitDetails: {
          operatorName: 'Test Operator',
          operatorAddress: {
            line1: '123 Test Street',
            town: 'Test Town',
            postcode: 'TE1 1ST',
            country: 'GB',
          },
          responsiblePersonDetails: {
            firstName: 'John',
            lastName: 'Doe',
            email: 'john@example.com',
            address: {
              line1: '123 Test Street',
              town: 'Test Town',
              postcode: 'TE1 1ST',
              country: 'GB',
            },
          },
        },
      },
    },
    requestTaskId: 123,
    sectionsCompleted: { [REVIEW_TARGET_UNIT_DETAILS_SUBTASK]: 'IN_PROGRESS' },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewTargetUnitDetailsCheckYourAnswersComponent, RouterModule.forRoot([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: route },
        {
          provide: Router,
          useValue: {
            navigate: jest.fn(),
            url: '/test/check-your-answers',
            events: of({}),
            createUrlTree: () => ({}),
            serializeUrl: () => '',
          },
        },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Vary the underlying agreement' },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    store = TestBed.inject(RequestTaskStore);
    store.setState(enhancedMockState);

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

  it('should submit form and call saveRequestTaskAction with completed section', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    const user = UserEvent.setup();
    const confirmButton = screen.getByText('Confirm and complete');
    await user.click(confirmButton);

    await waitFor(() => {
      expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);
    });

    const callArg = saveRequestTaskActionSpy.mock.calls[0][0];
    expect(callArg.requestTaskActionPayload.sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK]).toBe('COMPLETED');

    expect(router.navigate).toHaveBeenCalledWith(['../../..'], { relativeTo: route });
  });
});

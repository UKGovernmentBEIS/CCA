import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState, TasksApiService } from '@requests/common';
import { screen, waitFor } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { ResponsiblePersonComponent } from './responsible-person.component';

const enhancedMockState = {
  ...mockRequestTaskState,
  payload: {
    underlyingAgreement: {
      underlyingAgreementTargetUnitDetails: {
        operatorName: 'Test Operator',
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
  sectionsCompleted: {},
};

describe('ResponsiblePersonComponent', () => {
  let component: ResponsiblePersonComponent;
  let fixture: ComponentFixture<ResponsiblePersonComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub();

  const tasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const saveRequestTaskActionSpy = jest.spyOn(tasksApiService, 'saveRequestTaskAction');

  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponsiblePersonComponent, RouterModule.forRoot([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: route },
        {
          provide: Router,
          useValue: {
            navigate: jest.fn(),
            url: '/test/responsible-person',
            events: of({}),
            createUrlTree: () => ({}),
            serializeUrl: () => '',
          },
        },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Vary the underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    store.setState(enhancedMockState);

    fixture = TestBed.createComponent(ResponsiblePersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = screen.getByRole('heading', { name: 'Responsible person' });
    expect(heading).toBeInTheDocument();
  });

  it('should render the responsible person input fields', () => {
    expect(screen.getByText('Email address')).toBeInTheDocument();
    expect(screen.getByText('First name')).toBeInTheDocument();
    expect(screen.getByText('Last name')).toBeInTheDocument();
    expect(screen.getByText('Job title (optional)')).toBeInTheDocument();
    expect(screen.getAllByText('Phone number')).toHaveLength(2);
    expect(screen.getByText('The responsible person address is the same as the operator address')).toBeInTheDocument();
  });

  it('should submit form and call saveRequestTaskAction method', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    const user = UserEvent.setup();
    const continueButton = screen.getByRole('button', { name: /Continue/i });
    await user.click(continueButton);

    expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);
  });

  it('should call the saveRequestTaskAction method when the form is updated in a valid way and then submitted', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    const user = UserEvent.setup();

    const emailInput = screen.getByLabelText(/Email address/i);
    const firstNameInput = screen.getByLabelText(/First name/i);
    const lastNameInput = screen.getByLabelText(/Last name/i);

    await user.clear(emailInput);
    await user.type(emailInput, 'newemail@test.com');

    await user.clear(firstNameInput);
    await user.type(firstNameInput, 'NewFirstName');

    await user.clear(lastNameInput);
    await user.type(lastNameInput, 'NewLastName');

    const continueButton = screen.getByRole('button', { name: /Continue/i });
    await user.click(continueButton);

    await waitFor(() => {
      expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);
    });

    const callArg = saveRequestTaskActionSpy.mock.calls[0][0];
    expect(callArg.requestTaskActionType).toBe('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SAVE_APPLICATION');

    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });
});

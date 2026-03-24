import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState, TasksApiService } from '@requests/common';
import { clear, click, getAllByText, getByLabelText, getByRole, getByText, type } from '@testing';

import { ResponsiblePersonComponent } from './responsible-person.component';

// Enhanced mock state with required properties
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
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply for underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    // Use the enhanced mock state
    store.setState(enhancedMockState);

    fixture = TestBed.createComponent(ResponsiblePersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = getByRole('heading', { name: 'Responsible person' });
    expect(heading).toBeTruthy();
  });

  it('should render the responsible person input fields', () => {
    expect(getByText('Email address')).toBeTruthy();
    expect(getByText('First name')).toBeTruthy();
    expect(getByText('Last name')).toBeTruthy();
    expect(getByText('Job title (optional)')).toBeTruthy();
    expect(getAllByText('Phone number')).toHaveLength(2);
    expect(getByText('The responsible person address is the same as the operator address')).toBeTruthy();
  });

  it('should submit form and call saveRequestTaskAction method', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    const continueButton = getByRole('button', { name: /Continue/i });
    click(continueButton);
    await fixture.whenStable();

    expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);
  });

  it('should call the saveRequestTaskAction method when the form is updated in a valid way and then submitted', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    // Properly select form inputs by targeting input elements
    const emailInput = getByLabelText(/Email address/i) as HTMLInputElement;
    const firstNameInput = getByLabelText(/First name/i) as HTMLInputElement;
    const lastNameInput = getByLabelText(/Last name/i) as HTMLInputElement;

    // Clear and type into the inputs
    clear(emailInput);
    type(emailInput, 'newemail@test.com');

    clear(firstNameInput);
    type(firstNameInput, 'NewFirstName');

    clear(lastNameInput);
    type(lastNameInput, 'NewLastName');

    // Click the continue button
    const continueButton = getByRole('button', { name: /Continue/i });
    click(continueButton);
    await fixture.whenStable();

    expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);

    // Verify the payload contains the expected action type
    const callArg = saveRequestTaskActionSpy.mock.calls[0][0];
    expect(callArg.requestTaskActionType).toBe('UNDERLYING_AGREEMENT_SAVE_APPLICATION');

    // Verify navigation to check-your-answers page
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });
});

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

import { OperatorAddressComponent } from './operator-address.component';

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
      },
    },
  },
  requestTaskId: 123,
  sectionsCompleted: {},
};

describe('OperatorAddressComponent', () => {
  let component: OperatorAddressComponent;
  let fixture: ComponentFixture<OperatorAddressComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const tasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const saveRequestTaskActionSpy = jest.spyOn(tasksApiService, 'saveRequestTaskAction');

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorAddressComponent, RouterModule.forRoot([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: route },
        {
          provide: Router,
          useValue: {
            navigate: jest.fn(),
            url: '/test/operator-address',
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

    fixture = TestBed.createComponent(OperatorAddressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', async () => {
    const heading = screen.getByRole('heading', { name: 'Operator address' });
    expect(heading).toBeInTheDocument();
  });

  it('should render the address input fields', async () => {
    const addressInput = screen.getByTestId('account-address-input');
    expect(addressInput).toBeInTheDocument();
    expect(screen.getByText('Address line 1')).toBeInTheDocument();
    expect(screen.getByText('Address line 2 (optional)')).toBeInTheDocument();
    expect(screen.getByText('Town or city')).toBeInTheDocument();
    expect(screen.getByText('County (optional)')).toBeInTheDocument();
    expect(screen.getByText('Postcode')).toBeInTheDocument();
    expect(screen.getByText('Country')).toBeInTheDocument();
  });

  it('should submit form and call saveRequestTaskAction method, then navigate to next step', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    const user = UserEvent.setup();
    const continueButton = screen.getByRole('button', { name: /Continue/i });
    await user.click(continueButton);

    await waitFor(() => {
      expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);
    });

    const callArg = saveRequestTaskActionSpy.mock.calls[0][0];
    expect(callArg.requestTaskActionType).toBe('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SAVE_APPLICATION');

    expect(router.navigate).toHaveBeenCalledWith(['../responsible-person'], { relativeTo: route });
  });

  it('should update operator address when form is filled and submitted', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    const user = UserEvent.setup();

    // Get form inputs by label text
    const addressLine1Input = screen.getByLabelText('Address line 1');
    const townInput = screen.getByLabelText('Town or city');
    const postcodeInput = screen.getByLabelText('Postcode');

    // Clear and fill in new values
    await user.clear(addressLine1Input);
    await user.type(addressLine1Input, '456 New Street');

    await user.clear(townInput);
    await user.type(townInput, 'New Town');

    await user.clear(postcodeInput);
    await user.type(postcodeInput, 'NE1 2WS');

    // Submit form
    const continueButton = screen.getByRole('button', { name: /Continue/i });
    await user.click(continueButton);

    await waitFor(() => {
      expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);
    });

    // Verify navigation
    expect(router.navigate).toHaveBeenCalledWith(['../responsible-person'], { relativeTo: route });
  });
});

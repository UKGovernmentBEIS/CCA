import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskState, TasksApiService } from '@requests/common';
import { clear, click, getByLabelText, getByRole, getByTestId, getByText, type } from '@testing';

import { OperatorAddressComponent } from './operator-address.component';

// Enhanced mock state with required properties
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
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Apply for underlying agreement' },
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
    const heading = getByRole('heading', { name: 'Operator address' });
    expect(heading).toBeTruthy();
  });

  it('should render the address input fields', async () => {
    const addressInput = getByTestId('account-address-input');
    expect(addressInput).toBeTruthy();
    expect(getByText('Address line 1')).toBeTruthy();
    expect(getByText('Address line 2 (optional)')).toBeTruthy();
    expect(getByText('Town or city')).toBeTruthy();
    expect(getByText('County (optional)')).toBeTruthy();
    expect(getByText('Postcode')).toBeTruthy();
    expect(getByText('Country')).toBeTruthy();
  });

  it('should submit form and call saveRequestTaskAction method, then navigate to next step', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    const continueButton = getByRole('button', { name: /Continue/i });
    click(continueButton);
    await fixture.whenStable();

    expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);

    // Verify payload
    const callArg = saveRequestTaskActionSpy.mock.calls[0][0];
    expect(callArg.requestTaskActionType).toBe('UNDERLYING_AGREEMENT_SAVE_APPLICATION');

    // Verify navigation
    expect(router.navigate).toHaveBeenCalledWith(['../responsible-person'], { relativeTo: route });
  });

  it('should update operator address when form is filled and submitted', async () => {
    saveRequestTaskActionSpy.mockClear();
    saveRequestTaskActionSpy.mockReturnValue(of({}));

    // Get form inputs by label text
    const addressLine1Input = getByLabelText('Address line 1') as HTMLInputElement;
    const townInput = getByLabelText('Town or city') as HTMLInputElement;
    const postcodeInput = getByLabelText('Postcode') as HTMLInputElement;

    // Clear and fill in new values
    clear(addressLine1Input);
    type(addressLine1Input, '456 New Street');

    clear(townInput);
    type(townInput, 'New Town');

    clear(postcodeInput);
    type(postcodeInput, 'NE1 2WS');

    // Submit form
    const continueButton = getByRole('button', { name: /Continue/i });
    click(continueButton);
    await fixture.whenStable();

    expect(saveRequestTaskActionSpy).toHaveBeenCalledTimes(1);

    // Verify navigation
    expect(router.navigate).toHaveBeenCalledWith(['../responsible-person'], { relativeTo: route });
  });
});

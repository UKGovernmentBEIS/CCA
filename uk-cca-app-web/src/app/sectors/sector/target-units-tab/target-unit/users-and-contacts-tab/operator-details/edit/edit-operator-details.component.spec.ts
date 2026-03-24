import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getAllByText, getByLabelText, getByTestId, getByText, setInputValue } from '@testing';

import { mockTargetUnitOperatorDetails } from '../../../../../../specs/fixtures/mock';
import { ActiveOperatorStore } from '../active-operator.store';
import { EditOperatorDetailsComponent } from './edit-operator-details.component';

describe('EditOperatorDetailsComponent', () => {
  let fixture: ComponentFixture<EditOperatorDetailsComponent>;
  let store: ActiveOperatorStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditOperatorDetailsComponent],
      providers: [ActiveOperatorStore, provideHttpClient(), provideHttpClientTesting()],
    })
      .overrideProvider(ActivatedRoute, {
        useValue: new ActivatedRouteStub({ targetUnitId: 1, userId: 'e7de58d5-0256-42a7-9501-014d25d5d310' }),
      })
      .compileComponents();

    store = TestBed.inject(ActiveOperatorStore);
    store.setState({
      details: mockTargetUnitOperatorDetails,
      editable: true,
    });

    fixture = TestBed.createComponent(EditOperatorDetailsComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(getByTestId('operator-details-form')).toBeTruthy();
  });

  it('should render title', () => {
    expect(getByText('Change user details')).toBeTruthy();
  });

  it('should contain 7 inputs', () => {
    const inputs = document.querySelectorAll('.govuk-input');
    expect(inputs.length).toBe(7);
  });

  it('should correctly fill input values', () => {
    const firstName = getByLabelText('First name');
    expect((firstName as HTMLInputElement).value).toBe('oper1');

    const lastName = getByLabelText('Last name');
    expect((lastName as HTMLInputElement).value).toBe('tu');

    const jobTitle = getByLabelText('Job title (optional)');
    expect((jobTitle as HTMLInputElement).value).toBe('job12');

    const organisationName = getByLabelText('Organisation name (optional)');
    expect((organisationName as HTMLInputElement).value).toBe('organisation');

    const email = getByLabelText('Email address');
    expect((email as HTMLInputElement).value).toBe('op1tu@cca.uk');

    expect((getByLabelText('Consultant') as HTMLInputElement).checked).toBe(true);

    expect((getByLabelText('Phone number 1') as HTMLInputElement).value).toBe('1234567890');
    expect((getByLabelText('Phone number 2') as HTMLInputElement).value).toBe('1234567890');
  });

  it('should display form errors for mandatory fields', () => {
    const form = getByTestId('operator-details-form') as HTMLFormElement;
    form.reset();
    setInputValue(getByLabelText('First name') as HTMLInputElement, '');
    setInputValue(getByLabelText('Last name') as HTMLInputElement, '');
    fixture.detectChanges();
    click(getByText('Confirm and continue'));
    fixture.detectChanges();

    expect(getAllByText('Enter the first name').length).toBe(2);
    expect(getAllByText('Enter the last name').length).toBe(2);
  });
});

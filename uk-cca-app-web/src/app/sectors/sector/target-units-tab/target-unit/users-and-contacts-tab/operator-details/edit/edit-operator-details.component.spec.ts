import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { mockTargetUnitOperatorDetails } from 'src/app/sectors/specs/fixtures/mock';

import { ActiveOperatorStore } from '../active-operator.store';
import { EditOperatorDetailsComponent } from './edit-operator-details.component';

describe('EditOperatorDetailsComponent', () => {
  let store: ActiveOperatorStore;

  beforeEach(async () => {
    await render(EditOperatorDetailsComponent, {
      providers: [ActiveOperatorStore, provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, {
          useValue: new ActivatedRouteStub({ targetUnitId: 1, userId: 'e7de58d5-0256-42a7-9501-014d25d5d310' }),
        });
        store = testbed.inject(ActiveOperatorStore);
        store.setState({
          details: mockTargetUnitOperatorDetails,
          editable: true,
        });
      },
    });
  });

  it('should create', () => {
    expect(screen.getByTestId('operator-details-form')).toBeInTheDocument();
  });

  it('should render title', () => {
    expect(screen.getByText('Change user details')).toBeInTheDocument();
  });

  it('should contain 7 inputs', () => {
    const inputs = document.querySelectorAll('.govuk-input');
    expect(inputs.length).toBe(7);
  });

  it('should correctly fill input values', async () => {
    const firstName = screen.getByLabelText('First name');
    expect((firstName as HTMLInputElement).value).toBe('oper1');

    const lastName = screen.getByLabelText('Last name');
    expect((lastName as HTMLInputElement).value).toBe('tu');

    const jobTitle = screen.getByLabelText('Job title (optional)');
    expect((jobTitle as HTMLInputElement).value).toBe('job12');

    const organisationName = screen.getByLabelText('Organisation name (optional)');
    expect((organisationName as HTMLInputElement).value).toBe('organisation');

    const email = screen.getByLabelText('Email address');
    expect((email as HTMLInputElement).value).toBe('op1tu@cca.uk');

    expect(screen.getByLabelText('Consultant')).toBeChecked();

    expect(screen.getByLabelText('Phone number 1')).toHaveValue('1234567890');
    expect(screen.getByLabelText('Phone number 2')).toHaveValue('1234567890');
  });

  it('should display form errors for mandatory fields', async () => {
    const user = UserEvent.setup();
    const form = screen.getByTestId('operator-details-form') as HTMLFormElement;
    form.reset();
    await user.click(screen.getByText('Confirm and continue'));

    expect(screen.getAllByText('Enter the first name')).toHaveLength(2);
    expect(screen.getAllByText('Enter the last name')).toHaveLength(2);
  });
});

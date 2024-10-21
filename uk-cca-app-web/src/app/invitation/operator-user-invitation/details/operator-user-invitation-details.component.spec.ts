import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { OperatorUserInvitationFormProvider } from '../form.provider';
import { InvitedOperatorUserExtended, OperatorUserInvitationStore } from '../store';
import { OperatorUserInvitationComponent } from './operator-user-invitation-details.component';

describe('SectorUserInvitationComponent', () => {
  let operatorUserInvitationStore: OperatorUserInvitationStore;

  const route = new ActivatedRouteStub();

  const operatorUserStoreState: InvitedOperatorUserExtended = {
    firstName: 'name',
    lastName: 'surname',
    jobTitle: 'job',
    contactType: 'CONSULTANT',
    roleCode: 'operator_user_basic_user',
    email: 'test@example.com',
    emailToken: 'aslfijmaslifhmsalf',
    organisationName: 'organisation',
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  };

  beforeEach(async () => {
    await render(OperatorUserInvitationComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        OperatorUserInvitationStore,
        OperatorUserInvitationFormProvider,
      ],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, { useValue: route });
        operatorUserInvitationStore = testbed.inject(OperatorUserInvitationStore);
        operatorUserInvitationStore.setState(operatorUserStoreState);
      },
    });
  });

  it('should create', () => {
    expect(screen.getByTestId('invited-sector-user-details-form')).toBeInTheDocument();
  });

  it('should populate the form with valid information', () => {
    expect(screen.getByLabelText('First name')).toHaveValue('name');
    expect(screen.getByLabelText('Last name')).toHaveValue('surname');
    expect(screen.getByLabelText('Job title (optional)')).toHaveValue('job');
    expect(screen.getByLabelText('Email address')).toHaveValue('test@example.com');
    expect(screen.getByLabelText('Consultant')).toBeChecked();
  });
});

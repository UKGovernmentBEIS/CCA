import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByLabelText, getByTestId } from '@testing';

import { OperatorUserInvitationFormProvider } from '../form.provider';
import { InvitedOperatorUserExtended, OperatorUserInvitationStore } from '../store';
import { OperatorUserInvitationComponent } from './operator-user-invitation-details.component';

describe('SectorUserInvitationComponent', () => {
  let fixture: ComponentFixture<OperatorUserInvitationComponent>;
  let operatorUserInvitationStore: OperatorUserInvitationStore;

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
    await TestBed.configureTestingModule({
      imports: [OperatorUserInvitationComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        OperatorUserInvitationStore,
        OperatorUserInvitationFormProvider,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(),
        },
      ],
    }).compileComponents();

    operatorUserInvitationStore = TestBed.inject(OperatorUserInvitationStore);
    operatorUserInvitationStore.setState(operatorUserStoreState);

    fixture = TestBed.createComponent(OperatorUserInvitationComponent);

    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(getByTestId('invited-sector-user-details-form')).toBeTruthy();
  });

  it('should populate the form with valid information', () => {
    expect((getByLabelText('First name') as HTMLInputElement).value).toBe('name');
    expect((getByLabelText('Last name') as HTMLInputElement).value).toBe('surname');
    expect((getByLabelText('Job title (optional)') as HTMLInputElement).value).toBe('job');
    expect((getByLabelText('Email address') as HTMLInputElement).value).toBe('test@example.com');
    expect((getByLabelText('Consultant') as HTMLInputElement).checked).toBe(true);
  });
});

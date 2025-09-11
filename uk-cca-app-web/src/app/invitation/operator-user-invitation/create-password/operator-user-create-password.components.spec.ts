import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { InvitedOperatorUserExtended, OperatorUserInvitationStore } from '../store';
import { OperatorUserCreatePasswordComponent } from './operator-user-create-password.component';

describe('SectorUserInvitationCreatePasswordComponent', () => {
  let component: OperatorUserCreatePasswordComponent;
  let fixture: ComponentFixture<OperatorUserCreatePasswordComponent>;
  let operatorUserInvitationStore: OperatorUserInvitationStore;
  let router: Router;

  const route = new ActivatedRouteStub();

  const operatorUserStoreState: InvitedOperatorUserExtended = {
    firstName: 'name',
    lastName: 'surname',
    jobTitle: 'job',
    contactType: 'CONSULTANT',
    roleCode: 'sector_user_basic_user',
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
      imports: [OperatorUserCreatePasswordComponent],
      providers: [
        OperatorUserInvitationStore,
        provideZxvbnServiceForPSM(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    operatorUserInvitationStore = TestBed.inject(OperatorUserInvitationStore);
    operatorUserInvitationStore.setState(operatorUserStoreState);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(OperatorUserCreatePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the form with no password information', () => {
    expect(screen.getByLabelText('Create a password to activate your account')).toHaveValue('');
    expect(screen.getByLabelText('Re-enter your password')).toHaveValue('');
  });

  it('should show form errors', async () => {
    const user = UserEvent.setup();
    await user.type(screen.getByLabelText('Create a password to activate your account'), '123');
    await user.type(screen.getByLabelText('Re-enter your password'), '456');

    await user.click(screen.getByText('Continue'));
    fixture.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeInTheDocument();
  });

  it('should submit the form', async () => {
    const user = UserEvent.setup();
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const stateSpy = jest.spyOn(operatorUserInvitationStore, 'updateState');
    await user.type(screen.getByLabelText('Create a password to activate your account'), 'ThisIsAStrongP@ssw0rd');
    await user.type(screen.getByLabelText('Re-enter your password'), 'ThisIsAStrongP@ssw0rd');

    await user.click(screen.getByText('Continue'));
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(stateSpy).toHaveBeenCalledTimes(1);
  });
});

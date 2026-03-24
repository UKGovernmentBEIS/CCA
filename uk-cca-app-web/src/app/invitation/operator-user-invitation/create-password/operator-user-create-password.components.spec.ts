import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByLabelText, getByText, type } from '@testing';

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
    expect(
      (
        getByLabelText('Create a password to activate your account', fixture.nativeElement) as
          | HTMLInputElement
          | HTMLSelectElement
          | null
      )?.value ?? '',
    ).toBe('');
    expect(
      (getByLabelText('Re-enter your password', fixture.nativeElement) as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe('');
  });

  it('should show form errors', async () => {
    type(
      getByLabelText('Create a password to activate your account', fixture.nativeElement) as HTMLInputElement,
      '123',
    );
    type(getByLabelText('Re-enter your password', fixture.nativeElement) as HTMLInputElement, '456');

    click(getByText('Continue', fixture.nativeElement));
    fixture.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
  });

  it('should submit the form', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const stateSpy = jest.spyOn(operatorUserInvitationStore, 'updateState');
    type(
      getByLabelText('Create a password to activate your account', fixture.nativeElement) as HTMLInputElement,
      'ThisIsAStrongP@ssw0rd',
    );
    type(getByLabelText('Re-enter your password', fixture.nativeElement) as HTMLInputElement, 'ThisIsAStrongP@ssw0rd');

    click(getByText('Continue', fixture.nativeElement));
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(stateSpy).toHaveBeenCalledTimes(1);
  });
});

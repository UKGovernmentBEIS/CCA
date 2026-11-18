import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByTestId, getByText, setInputValue } from '@testing';

import { OperatorUsersInvitationService } from 'cca-api';

import { AddOperatorComponent } from './add-operator.component';

describe('Add operator Component', () => {
  let fixture: ComponentFixture<AddOperatorComponent>;
  let inviteOperatorUserToAccountMock: ReturnType<typeof vi.fn>;
  let router: Router;
  let routerNavigateMock: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    inviteOperatorUserToAccountMock = vi.fn();

    await TestBed.configureTestingModule({
      imports: [AddOperatorComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    })
      .overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ targetUnitId: 1 }) })
      .overrideProvider(OperatorUsersInvitationService, {
        useValue: {
          inviteOperatorUserToAccount: inviteOperatorUserToAccountMock,
        },
      })
      .compileComponents();

    router = TestBed.inject(Router);
    routerNavigateMock = vi.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(AddOperatorComponent);
    fixture.detectChanges();
  });

  it('should render preform content', () => {
    expect(getByText('Add an operator user')).toBeTruthy();
    expect(getByText('This user will have permission to:')).toBeTruthy();
    expect(getByText('view other operator users')).toBeTruthy();
    expect(getByText('view all tasks related to this target unit and its facilities')).toBeTruthy();
    expect(getByText('view target period reports')).toBeTruthy();
    expect(getByText('You should only add a user authorised to perform these actions.')).toBeTruthy();
  });

  it('should render form', () => {
    expect(getByTestId('add-operator-form')).toBeTruthy();
    expect(document.getElementById('firstName')).toBeTruthy();
    expect(document.getElementById('lastName')).toBeTruthy();
    expect(document.getElementById('email')).toBeTruthy();
    expect(document.getElementById('contactType')).toBeTruthy();
  });

  it('should render error when operator already exists', () => {
    setInputValue(document.getElementById('firstName') as HTMLInputElement, 'Operator');
    setInputValue(document.getElementById('lastName') as HTMLInputElement, 'Admin');
    setInputValue(document.getElementById('email') as HTMLInputElement, 'regulator_admin@cca.uk');

    fixture.detectChanges();

    inviteOperatorUserToAccountMock.mockReturnValueOnce(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 400,
            error: {
              code: 'AUTHORITY1016',
              message: '',
              security: true,
              data: [[]],
            },
          }),
      ),
    );

    click(getByText('Submit'));
    fixture.detectChanges();

    const msg =
      'This email address is already in use. You must enter a different email address for this user to add them as an operator user';
    const inline = Array.from(document.querySelectorAll('.govuk-error-message')).filter((el) =>
      el.textContent?.includes(msg),
    ).length;
    const summary = Array.from(document.querySelectorAll('.govuk-error-summary__list a')).filter((el) =>
      el.textContent?.includes(msg),
    ).length;

    expect(inline + summary).toBe(2);
  });

  it('should submit valid form and navigate to confirmation', () => {
    setInputValue(document.getElementById('firstName') as HTMLInputElement, 'Operator');
    setInputValue(document.getElementById('lastName') as HTMLInputElement, 'User');
    setInputValue(document.getElementById('email') as HTMLInputElement, 'operator_user_1@cca.uk');

    fixture.detectChanges();
    inviteOperatorUserToAccountMock.mockReturnValueOnce(of({}));
    click(getByText('Submit'));
    fixture.detectChanges();

    expect(inviteOperatorUserToAccountMock).toHaveBeenCalledWith(
      1,
      expect.objectContaining({
        firstName: 'Operator',
        lastName: 'User',
        email: 'operator_user_1@cca.uk',
      }),
    );

    expect(routerNavigateMock).toHaveBeenCalledWith(['../confirmation'], { relativeTo: expect.anything() });
  });

  it('should show errors when mandatory fields are missing', () => {
    setInputValue(document.getElementById('firstName') as HTMLInputElement, 'Operator');
    setInputValue(document.getElementById('lastName') as HTMLInputElement, '');
    setInputValue(document.getElementById('email') as HTMLInputElement, '');

    fixture.detectChanges();
    click(getByText('Submit'));
    fixture.detectChanges();

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();

    expect(
      Array.from(document.querySelectorAll('.govuk-error-message')).filter((el) =>
        el.textContent?.includes('Enter your last name'),
      ).length,
    ).toBeGreaterThanOrEqual(1);

    expect(
      Array.from(document.querySelectorAll('.govuk-error-message')).filter((el) =>
        el.textContent?.includes('Enter your email'),
      ).length,
    ).toBeGreaterThanOrEqual(1);
  });

  it('should show error for invalid email', () => {
    setInputValue(document.getElementById('firstName') as HTMLInputElement, 'Operator');
    setInputValue(document.getElementById('lastName') as HTMLInputElement, 'User');
    setInputValue(document.getElementById('email') as HTMLInputElement, 'operator_user_1');

    fixture.detectChanges();
    click(getByText('Submit'));
    fixture.detectChanges();

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();

    expect(
      Array.from(document.querySelectorAll('.govuk-error-message')).filter((el) =>
        el.textContent?.includes('Enter an email address in the correct format, like name@example.com'),
      ).length,
    ).toBeGreaterThanOrEqual(1);
  });
});

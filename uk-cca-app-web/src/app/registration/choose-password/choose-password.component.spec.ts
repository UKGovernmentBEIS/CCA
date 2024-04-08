import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass, MockType } from '@testing';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { OperatorUsersRegistrationService } from 'cca-api';

import { PasswordComponent } from 'src/app/shared-user/password/password.component';

import { PasswordService } from '../../shared-user/password/password.service';
import { UserRegistrationStore } from '../store/user-registration.store';
import { ChoosePasswordComponent } from './choose-password.component';

describe('ChoosePasswordComponent', () => {
  let component: ChoosePasswordComponent;
  let fixture: ComponentFixture<ChoosePasswordComponent>;
  let page: Page;
  let passwordService: jest.Mocked<PasswordService>;

  class Page extends BasePage<ChoosePasswordComponent> {
    get emailValue() {
      return this.getInputValue('#email');
    }

    get passwordValue() {
      return this.getInputValue('#password');
    }

    set passwordValue(password: string) {
      this.setInputValue('#password', password);
    }

    get repeatedPasswordValue() {
      return this.query<HTMLInputElement>('#validatePassword').value;
    }

    set repeatedPasswordValue(password: string) {
      this.setInputValue('#validatePassword', password);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const operatorUsersRegistrationService: MockType<OperatorUsersRegistrationService> = {
    enableOperatorInvitedUser: jest.fn().mockReturnValue(of(null)),
    acceptOperatorInvitation: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    passwordService = mockClass(PasswordService);

    await TestBed.configureTestingModule({
      imports: [SharedModule, PageHeadingComponent, PasswordComponent, RouterTestingModule],
      declarations: [ChoosePasswordComponent],
      providers: [
        UserRegistrationStore,
        { provide: OperatorUsersRegistrationService, useValue: operatorUsersRegistrationService },
        { provide: PasswordService, useValue: passwordService },
        provideZxvbnServiceForPSM(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChoosePasswordComponent);
    component = fixture.debugElement.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from store', inject(
    [UserRegistrationStore],
    fakeAsync((store: UserRegistrationStore) => {
      passwordService.blacklisted.mockReturnValue(of(null));
      store.setState({ password: 'password', email: 'test@cca.uk' });

      tick();
      fixture.detectChanges();

      expect(page.emailValue).toBe('test@cca.uk');
      expect(page.passwordValue).toBe('password');
      expect(page.repeatedPasswordValue).toBe('password');
    }),
  ));

  it('should submit only if form valid', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    passwordService.blacklisted.mockReturnValue(of(null));

    page.passwordValue = '';
    page.repeatedPasswordValue = '';
    page.submitButton.click();
    fixture.detectChanges();

    page.passwordValue = 'test';
    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.passwordValue = 'ThisIsAStrongP@ssw0rd';
    page.repeatedPasswordValue = 'ThisIsAStrongP@ssw0rd';

    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalled();
  }));

  it('should navigate to summary when creating an operator from an emitter', inject(
    [Router, UserRegistrationStore],
    (router: Router, store: UserRegistrationStore) => {
      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
      const token = 'thisisatoken';
      const password = 'ThisIsAStrongP@ssw0rd';

      passwordService.blacklisted.mockReturnValue(of(null));

      store.setState({
        invitationStatus: 'PENDING_USER_ENABLE',
        token: token,
        password: password,
      });

      page.passwordValue = password;
      page.repeatedPasswordValue = password;

      page.submitButton.click();
      fixture.detectChanges();

      expect(operatorUsersRegistrationService.enableOperatorInvitedUser).toHaveBeenCalledWith({
        invitationToken: token,
        password: password,
      });
      expect(operatorUsersRegistrationService.acceptOperatorInvitation).toHaveBeenCalledWith({
        token,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../success'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    },
  ));
});

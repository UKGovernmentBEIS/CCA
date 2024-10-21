import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { PageNotFoundComponent } from '@error/page-not-found/page-not-found.component';
import { BasePage, mockClass, MockType } from '@netz/common/testing';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { ForgotPasswordService } from 'cca-api';

import { PasswordService } from '../../shared/components/password/password.service';
import { ResetPasswordStore } from '../store/reset-password.store';
import { ResetPasswordComponent } from './reset-password.component';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let page: Page;
  let router: Router;
  let passwordService: jest.Mocked<PasswordService>;
  let resetPasswordStore: ResetPasswordStore;

  class Page extends BasePage<ResetPasswordComponent> {
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

  const forgotPasswordService: MockType<ForgotPasswordService> = {
    verifyToken: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    passwordService = mockClass(PasswordService);

    await TestBed.configureTestingModule({
      imports: [
        ResetPasswordComponent,
        RouterTestingModule.withRoutes([
          { path: 'error/404', component: PageNotFoundComponent },
          { path: '', component: ResetPasswordComponent },
          { path: '**', redirectTo: '' },
        ]),
      ],
      providers: [
        ResetPasswordStore,
        { provide: ForgotPasswordService, useValue: forgotPasswordService },
        { provide: PasswordService, useValue: passwordService },
        provideZxvbnServiceForPSM(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    passwordService.blacklisted.mockReturnValue(of(null));
    resetPasswordStore = TestBed.inject(ResetPasswordStore);

    resetPasswordStore.setState({
      ...resetPasswordStore.state,
      password: 'password',
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get password from store', () => {
    forgotPasswordService.verifyToken.mockReturnValue(of({ email: 'test@mail.com' }));

    component.ngOnInit();
    fixture.detectChanges();
    expect(page.passwordValue).toEqual('password');
    expect(page.repeatedPasswordValue).toEqual('password');
  });

  it('should submit only if form valid', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

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
  });

  it('should navigate to appropriate page if there is an error', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    forgotPasswordService.verifyToken.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'EMAIL1001' } })),
    );

    component.ngOnInit();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['forgot-password', 'invalid-link']);

    forgotPasswordService.verifyToken.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'TOKEN1001' } })),
    );

    component.ngOnInit();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['error', '404']);
  });
});

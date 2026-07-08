import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { PageNotFoundComponent } from '@error/page-not-found/page-not-found.component';
import { BasePage, MockType } from '@netz/common/testing';

import { ForgotPasswordService, ValidatePasswordService } from 'cca-api';

import { ResetPasswordStore } from '../+store/reset-password.store';
import { ResetPasswordComponent } from './reset-password.component';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let page: Page;
  let router: Router;
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
    verifyToken: vi.fn().mockReturnValue(of({ email: 'test@mail.com' })),
  };

  const mockValidatePasswordService = { validatePassword: vi.fn().mockReturnValue(of(null)) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResetPasswordComponent],
      providers: [
        provideRouter([
          { path: 'error/404', component: PageNotFoundComponent },
          { path: '', component: ResetPasswordComponent },
          { path: '**', redirectTo: '' },
        ]),
        ResetPasswordStore,
        { provide: ForgotPasswordService, useValue: forgotPasswordService },
        { provide: ValidatePasswordService, useValue: mockValidatePasswordService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
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

  it('should navigate to appropriate page if there is an error', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
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

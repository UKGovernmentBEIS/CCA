import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { throwError } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { ActivatedRouteStub, BasePage, mockClass } from '@netz/common/testing';
import { PasswordComponent } from '@shared/components';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { RegulatorUsersRegistrationService } from 'cca-api';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';
import { RegulatorInvitationComponent } from './regulator-invitation.component';

describe('RegulatorInvitationComponent', () => {
  let component: RegulatorInvitationComponent;
  let fixture: ComponentFixture<RegulatorInvitationComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let regulatorUsersRegistrationService: jest.Mocked<RegulatorUsersRegistrationService>;
  let store: InvitedRegulatorUserStore;
  class Page extends BasePage<RegulatorInvitationComponent> {
    get emailValue() {
      return this.getInputValue<string>('#email');
    }

    set passwordValue(value: string) {
      this.setInputValue('#password', value);
    }

    set repeatedPasswordValue(value: string) {
      this.setInputValue('#validatePassword', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    regulatorUsersRegistrationService = mockClass(RegulatorUsersRegistrationService);
    const activatedRoute = new ActivatedRouteStub(undefined, { token: 'token' });

    await TestBed.configureTestingModule({
      imports: [RegulatorInvitationComponent, PasswordComponent, ReactiveFormsModule, PageHeadingComponent],
      providers: [
        { provide: RegulatorUsersRegistrationService, useValue: regulatorUsersRegistrationService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        InvitedRegulatorUserStore,
        provideZxvbnServiceForPSM(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegulatorInvitationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    store = TestBed.inject(InvitedRegulatorUserStore);
    store.setState({ email: 'user@cca.uk' });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate the form with email information', () => {
    expect(component.form.get('email').value).toEqual('user@cca.uk');
    expect(page.emailValue).toEqual('user@cca.uk');
  });

  it('should navigate for link related error', () => {
    regulatorUsersRegistrationService.acceptAuthorityAndActivateRegulatorUserFromInvite.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EMAIL1001' }, status: 400 })),
    );

    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    component.form.controls.password.setValue('ThisIsAStrongP@ssw0rd');
    component.form.get('validatePassword').setValue('ThisIsAStrongP@ssw0rd');
    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['invalid-link'], {
      relativeTo: route,
      queryParams: { code: 'EMAIL1001' },
    });

    regulatorUsersRegistrationService.acceptAuthorityAndActivateRegulatorUserFromInvite.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'TOKEN1001' }, status: 400 })),
    );
    component.form.controls.password.setValue('ThisIsAStrongP@ssw0rd');
    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(2);
    expect(navigateSpy).toHaveBeenCalledWith(['invalid-link'], {
      relativeTo: route,
      queryParams: { code: 'TOKEN1001' },
    });
  });

  it('should submit only if form valid', () => {
    page.passwordValue = '';
    page.repeatedPasswordValue = '';
    page.submitButton.click();
    fixture.detectChanges();

    expect(regulatorUsersRegistrationService.acceptAuthorityAndActivateRegulatorUserFromInvite).not.toHaveBeenCalled();

    page.passwordValue = 'test';
    page.submitButton.click();
    fixture.detectChanges();

    expect(regulatorUsersRegistrationService.acceptAuthorityAndActivateRegulatorUserFromInvite).not.toHaveBeenCalled();

    page.passwordValue = 'ThisIsAStrongP@ssw0rd';
    page.repeatedPasswordValue = 'ThisIsAStrongP@ssw0rd';
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(regulatorUsersRegistrationService.acceptAuthorityAndActivateRegulatorUserFromInvite).toHaveBeenCalledTimes(
      1,
    );
    expect(regulatorUsersRegistrationService.acceptAuthorityAndActivateRegulatorUserFromInvite).toHaveBeenCalledWith({
      invitationToken: 'token',
      password: 'ThisIsAStrongP@ssw0rd',
    });
  });
});

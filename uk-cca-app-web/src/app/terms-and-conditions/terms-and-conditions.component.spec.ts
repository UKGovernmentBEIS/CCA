import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';
import { buttonClick } from '@testing';

import { ApplicationUserDTO, UsersService } from 'cca-api';

import { LandingPageComponent } from '../landing-page/landing-page.component';
import { TermsAndConditionsComponent } from './terms-and-conditions.component';

describe('TermsAndConditionsComponent', () => {
  let component: TermsAndConditionsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let httpTestingController: HttpTestingController;
  let authStore: AuthStore;
  const authService: Partial<jest.Mocked<AuthService>> = {
    loadUser: jest.fn(() => of({} as ApplicationUserDTO)),
  };

  @Component({
    selector: 'cca-test',
    template: '<cca-terms-and-conditions></cca-terms-and-conditions>',
    standalone: true,
    imports: [TermsAndConditionsComponent],
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: '',
            component: LandingPageComponent,
          },
        ]),
        HttpClientTestingModule,
        TestComponent,
      ],
      providers: [UsersService, { provide: AuthService, useValue: authService }],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setState({
      ...authStore.getState(),
      isLoggedIn: true,
      userProfile: { firstName: 'Gimli', lastName: 'Gloin' },
      userState: {
        roleType: 'REGULATOR',
        userId: 'opTestId',
        status: 'ENABLED',
      },
      terms: { url: '/test', version: 1 },
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(TermsAndConditionsComponent)).componentInstance;
    fixture.detectChanges();
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have as title Accept terms and conditions', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toEqual('Terms And Conditions');
  });

  it('should contain a p tag with body', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelectorAll('p')[0].textContent.trim()).toEqual('ADD CONTENT HERE');
  });

  it('should enable button when checkbox is checked', () => {
    const compiled = fixture.debugElement.nativeElement;
    const checkbox = fixture.debugElement.queryAll(By.css('input'));
    checkbox[0].nativeElement.click();
    fixture.detectChanges();
    expect(compiled.querySelector('button').disabled).toBeFalsy();
  });

  it('should post if user accepts terms', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const checkbox = fixture.debugElement.query(By.css('input[type=checkbox]'));

    buttonClick(fixture);
    fixture.detectChanges();

    const compiled = fixture.debugElement.nativeElement;
    const errorSummary = compiled.querySelector('govuk-error-message').textContent.trim();
    expect(errorSummary).toEqual('Error: You should accept terms and conditions to proceed');

    checkbox.nativeElement.click();
    fixture.detectChanges();

    buttonClick(fixture);
    fixture.detectChanges();

    const request = httpTestingController.expectOne('/api/v1.0/users/terms-and-conditions');
    expect(request.request.method).toEqual('PATCH');

    request.flush(200);
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  }));
});

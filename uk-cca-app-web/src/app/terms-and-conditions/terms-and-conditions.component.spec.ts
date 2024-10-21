import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { LatestTermsStore } from '@core/store/latest-terms.store';
import { buttonClick } from '@netz/common/testing';

import { UsersService } from 'cca-api';

import { TermsAndConditionsComponent } from './terms-and-conditions.component';

describe('TermsAndConditionsComponent', () => {
  let component: TermsAndConditionsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let httpTestingController: HttpTestingController;
  let latestTermsStore: LatestTermsStore;

  const authService: Partial<jest.Mocked<AuthService>> = {
    loadUserTerms: jest.fn(() => of({})),
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
      imports: [TestComponent],
      providers: [
        UsersService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();

    latestTermsStore = TestBed.inject(LatestTermsStore);
    latestTermsStore.setLatestTerms({ url: '/test', version: 2 });
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

    const request = httpTestingController.expectOne('/api/v1.0/user-terms');
    expect(request.request.method).toEqual('PATCH');

    request.flush(200);
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  }));
});

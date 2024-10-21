import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { ActivatedRouteStub, changeInputValue } from '@netz/common/testing';
import { GovukComponentsModule } from '@netz/govuk-components';

import { CaExternalContactDTO, CaExternalContactsService } from 'cca-api';

import { saveNotFoundExternalContactError } from '../../errors/business-error';
import { ActiveExternalContactStore } from '../active-external-contact.store';
import { ExternalContactsDetailsComponent } from './details.component';

describe('DetailsComponent - add', () => {
  let component: ExternalContactsDetailsComponent;
  let fixture: ComponentFixture<ExternalContactsDetailsComponent>;
  let router: Router;
  let route: ActivatedRouteStub;
  const caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>> = {
    createCaExternalContact: jest.fn(),
    editCaExternalContact: jest.fn().mockReturnValue(of(null)),
  };
  const submitButton = () => fixture.nativeElement.querySelector('button[type="submit"]');
  const errorSummary = () => fixture.nativeElement.querySelector('govuk-error-summary');

  beforeEach(async () => {
    route = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [ExternalContactsDetailsComponent, GovukComponentsModule, BusinessTestingModule],
      providers: [
        ActiveExternalContactStore,
        provideRouter([]),
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExternalContactsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render add contact title and submit button if route contains no data', () => {
    expect(fixture.nativeElement.querySelector('h1').textContent.trim()).toEqual('Add an external contact');
    expect(submitButton().textContent.trim()).toEqual('Submit');
  });

  it('should show correct backend errors', () => {
    caExternalContactsService.createCaExternalContact.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1001' }, status: 400 })),
    );

    changeInputValue(fixture, '#name', 'Test name');
    changeInputValue(fixture, '#email', 'test@test.com');
    changeInputValue(fixture, '#description', 'Test');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('name').getError('uniqueName')).toEqual('Enter a unique displayed name');

    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('Enter a unique displayed name');

    changeInputValue(fixture, '#name', 'Test name');
    fixture.detectChanges();

    caExternalContactsService.createCaExternalContact.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1002' }, status: 400 })),
    );

    submitButton().click();
    fixture.detectChanges();

    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('email').getError('uniqueEmail')).toEqual('Email address already exists');

    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('Email address already exists');

    changeInputValue(fixture, '#email', 'test@email.test');
    fixture.detectChanges();

    caExternalContactsService.createCaExternalContact.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1003' }, status: 400 })),
    );

    submitButton().click();
    fixture.detectChanges();

    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('name').getError('uniqueName')).toEqual('Enter a unique displayed name');
    expect(component.form.get('email').getError('uniqueEmail')).toEqual('Email address already exists');

    expect(errorSummary()).toBeTruthy();
    expect(errorSummary().textContent).toContain('Email address already exists');
    expect(errorSummary().textContent).toContain('Enter a unique displayed name');
  });

  it('should navigate to external contacts on correct form submission', () => {
    caExternalContactsService.createCaExternalContact.mockReturnValue(of(null));
    const navigateSpy = jest.spyOn(router, 'navigate');

    changeInputValue(fixture, '#name', 'Test name');
    changeInputValue(fixture, '#email', 'test@test.com');
    changeInputValue(fixture, '#description', 'Test');
    fixture.detectChanges();

    submitButton().click();

    expect(caExternalContactsService.createCaExternalContact).toHaveBeenCalledTimes(1);
    expect(caExternalContactsService.createCaExternalContact).toHaveBeenCalledWith({
      name: 'Test name',
      email: 'test@test.com',
      description: 'Test',
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route, fragment: 'external-contacts' });
  });

  it('should not post form if invalid', () => {
    caExternalContactsService.createCaExternalContact.mockReturnValue(of(null));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const postExternalContactSpy = jest.spyOn(caExternalContactsService, 'createCaExternalContact');

    changeInputValue(fixture, '#name', 'Test name');
    changeInputValue(fixture, '#email', 'test');
    changeInputValue(fixture, '#description', 'Test');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(postExternalContactSpy).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('govuk-error-summary')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('govuk-error-summary').textContent).toContain(
      'Enter an email address in the correct format, like name@example.com',
    );

    changeInputValue(fixture, '#email', 'test@test.com');
    fixture.detectChanges();

    submitButton().click();

    expect(postExternalContactSpy).toHaveBeenCalledWith({
      name: 'Test name',
      email: 'test@test.com',
      description: 'Test',
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route, fragment: 'external-contacts' });
  });
});
describe('DetailsComponent - edit', () => {
  let store: ActiveExternalContactStore;
  let route: ActivatedRouteStub;
  let fixture: ComponentFixture<ExternalContactsDetailsComponent>;

  const submitButton = () => fixture.nativeElement.querySelector('button[type="submit"]');

  const caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>> = {
    createCaExternalContact: jest.fn(),
    editCaExternalContact: jest.fn().mockReturnValue(of(null)),
  };
  const mockExternalContact: CaExternalContactDTO = {
    id: 3,
    description: 'Description',
    email: 'external@contact.com',
    name: 'External Contact',
  };
  beforeEach(async () => {
    route = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [ExternalContactsDetailsComponent, GovukComponentsModule, BusinessTestingModule],
      providers: [
        ActiveExternalContactStore,
        provideRouter([]),
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    route.setParamMap({ userId: 1 });

    store = TestBed.inject(ActiveExternalContactStore);
    store.setState(mockExternalContact);

    fixture = TestBed.createComponent(ExternalContactsDetailsComponent);
    fixture.detectChanges();
  });

  it('should change title and button if route contains data', () => {
    expect(fixture.nativeElement.querySelector('h1').textContent.trim()).toEqual('External contact details');
    expect(submitButton().textContent.trim()).toEqual('Save');
  });

  it('should patch changed contact', () => {
    changeInputValue(fixture, '#name', 'New name');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    expect(caExternalContactsService.editCaExternalContact).toHaveBeenCalledTimes(1);
    expect(caExternalContactsService.editCaExternalContact).toHaveBeenCalledWith(1, {
      description: 'Description',
      email: 'external@contact.com',
      name: 'New name',
    });
  });

  it('should redirect with error if contact deleted', async () => {
    caExternalContactsService.editCaExternalContact.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EXTCONTACT1000' }, status: 400 })),
    );

    changeInputValue(fixture, '#name', 'New name');
    fixture.detectChanges();

    submitButton().click();
    fixture.detectChanges();

    await expectBusinessErrorToBe(saveNotFoundExternalContactError);
  });
});

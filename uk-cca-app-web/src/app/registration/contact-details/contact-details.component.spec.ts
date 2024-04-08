import { ChangeDetectorRef, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroupName } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { CountyService } from '@core/services/county.service';
import { CountyAddressInputComponent } from '@shared/county-address-input/county-address-input.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { SharedModule } from '@shared/shared.module';
import { UserInputComponent } from '@shared/user-input/user-input.component';
import { BasePage, CountryServiceStub, MockType } from '@testing';

import { OperatorUserRegistrationDTO } from 'cca-api';

import { UserRegistrationStore } from '../store/user-registration.store';
import { ContactDetailsComponent } from './contact-details.component';

const mockCountyService: MockType<CountyService> = {
  getUkCounties: jest.fn().mockReturnValue(
    of([
      {
        id: 1,
        name: 'Cyprus',
      },
      {
        id: 2,
        name: 'Greece',
      },
      {
        id: 3,
        name: 'Afghanistan',
      },
    ]),
  ),
};

describe('ContactDetailsComponent', () => {
  let component: ContactDetailsComponent;
  let fixture: ComponentFixture<ContactDetailsComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let store: UserRegistrationStore;
  let page: Page;

  class Page extends BasePage<ContactDetailsComponent> {
    get firstName() {
      return this.query<HTMLInputElement>('input[name="firstName"]');
    }

    get firstNameValue() {
      return this.getInputValue(this.firstName);
    }

    set firstNameValue(value: string) {
      this.setInputValue('input[name="firstName"]', value);
    }

    get lastName() {
      return this.query<HTMLInputElement>('input[name="lastName"]');
    }

    get lastNameValue() {
      return this.getInputValue(this.lastName);
    }

    set lastNameValue(value: string) {
      this.setInputValue('input[name="lastName"]', value);
    }

    get jobTitle() {
      return this.query<HTMLInputElement>('input[name="jobTitle"]');
    }

    get jobTitleValue() {
      return this.getInputValue(this.jobTitle);
    }

    set jobTitleValue(value: string) {
      this.setInputValue('input[name="jobTitle"]', value);
    }

    get countryCode() {
      return this.query<HTMLSelectElement>('select[name="phoneNumber.countryCode"]');
    }

    get countryCodeValue() {
      return this.getInputValue(this.countryCode)['countryCode'];
    }

    set countryCodeValue(value: string) {
      this.setInputValue('select[name="phoneNumber.countryCode"]', value);
    }

    get phoneNumber() {
      return this.query<HTMLInputElement>('input[name="phoneNumber"]');
    }

    get phoneNumberValue() {
      return this.getInputValue(this.phoneNumber);
    }

    set phoneNumberValue(value: string) {
      this.setInputValue('input[name="phoneNumber"]', value);
    }

    get email() {
      return this.query<HTMLInputElement>('input[name="email"]');
    }

    get emailValue() {
      return this.getInputValue(this.email);
    }

    get addressLine1() {
      return this.query<HTMLInputElement>('input[name="address.line1"]');
    }

    get addressLine1Value() {
      return this.getInputValue(this.addressLine1);
    }

    set addressLine1Value(value: string) {
      this.setInputValue('input[name="address.line1"]', value);
    }

    get addressLine2() {
      return this.query<HTMLInputElement>('input[name="address.line2"]');
    }

    get addressLine2Value() {
      return this.getInputValue('input[name="address.line2"]');
    }

    get addressCity() {
      return this.query<HTMLInputElement>('input[name="address.city"]');
    }

    get addressCityValue() {
      return this.getInputValue(this.addressCity);
    }

    set addressCityValue(value: string) {
      this.setInputValue('input[name="address.city"]', value);
    }

    get addressCounty() {
      return this.query<HTMLSelectElement>('select[name="address.county"]');
    }

    get addressCountyValue() {
      return this.getInputValue('select[name="address.county"]');
    }

    set addressCountyValue(value: string) {
      this.setInputValue('select[name="address.county"]', value);
    }

    get addressPostCode() {
      return this.query<HTMLInputElement>('input[name="address.postcode"]');
    }

    get addressPostCodeValue() {
      return this.getInputValue(this.addressPostCode);
    }

    set addressPostCodeValue(value: string) {
      this.setInputValue('input[name="address.postcode"]', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  @Component({ template: '' })
  class NoopComponent {}

  const mockContactDetails: Omit<OperatorUserRegistrationDTO, 'emailToken'> = {
    firstName: 'John',
    lastName: 'Doe',
    jobTitle: 'job title',
    address: {
      line1: 'Line',
      line2: null,
      city: 'City',
      county: 'Greece',
      postcode: 'PostCode',
    },
    phoneNumber: {
      countryCode: '30',
      number: '6946332211',
    },
    mobileNumber: {
      countryCode: null,
      number: null,
    },
  };

  // Making Angular aware of changes in component tests With OnPush Change Detection
  async function runOnPushChangeDetection(fixture: ComponentFixture<any>): Promise<void> {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContactDetailsComponent, NoopComponent],
      imports: [
        SharedModule,
        PageHeadingComponent,
        UserInputComponent,
        RouterTestingModule.withRoutes([{ path: 'choose-password', component: NoopComponent }]),
      ],
      providers: [
        UserRegistrationStore,
        { provide: CountyService, useValue: mockCountyService },
        { provide: CountryService, useClass: CountryServiceStub },
      ],
    })
      .overrideComponent(CountyAddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContactDetailsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    store = TestBed.inject(UserRegistrationStore);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from state', async () => {
    store.setState({ userRegistrationDTO: mockContactDetails, email: 'test@email.com' });
    await runOnPushChangeDetection(fixture);

    expect(page.firstNameValue).toBe(mockContactDetails.firstName);
    expect(page.lastNameValue).toBe(mockContactDetails.lastName);
    expect(page.jobTitleValue).toBe(mockContactDetails.jobTitle);
    expect(page.countryCodeValue).toEqual(mockContactDetails.phoneNumber.countryCode);
    expect(page.phoneNumberValue).toBe(mockContactDetails.phoneNumber.number);
    expect(page.emailValue).toBe('test@email.com');
    expect(page.addressLine1Value).toBe(mockContactDetails.address.line1);
    expect(page.addressLine2Value).toBeFalsy();
    expect(page.addressCityValue).toBe(mockContactDetails.address.city);
    expect(page.addressCountyValue).toBe(mockContactDetails.address.county);
    expect(page.addressPostCodeValue).toBe(mockContactDetails.address.postcode);
    expect(page.email.disabled).toBeTruthy();
    expect(page.addressLine1.disabled).toBeFalsy();
    expect(page.addressLine2.disabled).toBeFalsy();
    expect(page.addressCity.disabled).toBeFalsy();
    expect(page.addressCounty.disabled).toBeFalsy();
    expect(page.addressPostCode.disabled).toBeFalsy();
  });

  it('should not submit on invalid form', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.firstNameValue = 'first name';
    page.lastNameValue = 'last name';
    page.jobTitleValue = 'Any';
    page.countryCodeValue = '30';

    page.submitButton.click();
    await runOnPushChangeDetection(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();
    expect(component.isSummaryDisplayed).toBeTruthy();
    page.countryCodeValue = '30';
    page.phoneNumberValue = '306946332211';
    page.addressLine1Value = 'Any address';
    page.addressCityValue = 'City';
    page.addressCountyValue = 'Greece';
    page.addressPostCodeValue = '12345';

    page.submitButton.click();
    await runOnPushChangeDetection(fixture);

    expect(navigateSpy).toHaveBeenCalledWith(['../choose-password'], { relativeTo: route });
  });

  it('should navigate to choose password', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    store.setState({ userRegistrationDTO: mockContactDetails, isSummarized: false });

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    expect(store.getState().userRegistrationDTO).toEqual(mockContactDetails);
    expect(navigateSpy).toHaveBeenCalledWith(['../choose-password'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });

  it('should navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    store.setState({ userRegistrationDTO: mockContactDetails, isSummarized: true });

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    expect(store.getState().userRegistrationDTO).toEqual(mockContactDetails);
    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });

  it('should navigate to summary for emitter users', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const token = 'thisisatoken';

    store.setState({
      ...store.getState(),
      userRegistrationDTO: mockContactDetails,
      invitationStatus: 'PENDING_USER_REGISTRATION_NO_PASSWORD',
      token,
    });

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
      relativeTo: TestBed.inject(ActivatedRoute),
    });
  });
});

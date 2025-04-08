import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@netz/common/testing';
import { CountryService } from '@shared/services';

import { UpdateTargetUnitAccountService } from 'cca-api';

import { CountryServiceStub } from 'src/testing/country.service.stub';

import { mockTargetUnitAccountDetails } from '../../../../../specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { EditAdministrativeContactComponent } from './edit-administrative-contact.component';

describe('EditAdministrativeContactComponent', () => {
  let component: EditAdministrativeContactComponent;
  let fixture: ComponentFixture<EditAdministrativeContactComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;
  let store: ActiveTargetUnitStore;

  const updateTargetUnitAccountService = mockClass(UpdateTargetUnitAccountService);
  updateTargetUnitAccountService.updateTargetUnitAccountAdministrativePerson.mockReturnValue(of({}));

  class Page extends BasePage<EditAdministrativeContactComponent> {
    get firstNameValue() {
      return this.getInputValue('#firstName');
    }
    set firstNameValue(value: string) {
      this.setInputValue('#firstName', value);
    }

    get lastNameValue() {
      return this.getInputValue('#lastName');
    }

    get jobTitleValue() {
      return this.getInputValue('#jobTitle');
    }

    get phoneNumberValue() {
      return this.getInputValue('#phoneNumber');
    }

    get emailValue() {
      return this.getInputValue('#email');
    }

    get addressLine1Value() {
      return this.getInputValue(this.addressLine1);
    }

    get addressLine2Value() {
      return this.getInputValue(this.addressLine2);
    }

    get addressCityValue() {
      return this.getInputValue(this.addressCity);
    }

    get addressCountyValue() {
      return this.getInputValue(this.addressCounty);
    }

    get addressPostCodeValue() {
      return this.getInputValue(this.addressPostCode);
    }

    get addressLine1() {
      return this.query<HTMLInputElement>('#address\\.line1');
    }

    get addressLine2() {
      return this.query<HTMLInputElement>('#address\\.line2');
    }

    get addressCity() {
      return this.query<HTMLInputElement>('#address\\.city');
    }

    get addressCounty() {
      return this.query<HTMLSelectElement>('#address\\.county');
    }

    get addressPostCode() {
      return this.query<HTMLInputElement>('#address\\.postcode');
    }

    get addressCountrySelect(): HTMLSelectElement {
      return this.query('#address\\.country');
    }
    get addressCountryValue(): string {
      return this.addressCountrySelect.value;
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub({ targetUnitId: '1' });
    await TestBed.configureTestingModule({
      imports: [EditAdministrativeContactComponent],
      providers: [
        ActiveTargetUnitStore,
        { provide: UpdateTargetUnitAccountService, useValue: updateTargetUnitAccountService },
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(ActiveTargetUnitStore);
    store.setState({ targetUnitAccountDetails: mockTargetUnitAccountDetails });
    fixture = TestBed.createComponent(EditAdministrativeContactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the pre-populated form', () => {
    expect(page.firstNameValue).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.firstName);
    expect(page.lastNameValue).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.lastName);
    expect(page.jobTitleValue).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.jobTitle);
    expect(page.phoneNumberValue).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.phoneNumber.number);
    expect(page.emailValue).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.email);
    expect(page.addressLine1Value).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.address.line1);
    expect(page.addressLine2Value).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.address.line2);
    expect(page.addressCityValue).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.address.city);
    expect(page.addressCountyValue).toEqual(mockTargetUnitAccountDetails.administrativeContactDetails.address.county);
    expect(page.addressPostCodeValue).toEqual(
      mockTargetUnitAccountDetails.administrativeContactDetails.address.postcode,
    );
    expect(page.addressCountryValue).toEqual('3: GR');
    expect(page.addressLine1.disabled).toBeFalsy();
    expect(page.addressLine2.disabled).toBeFalsy();
    expect(page.addressCity.disabled).toBeFalsy();
    expect(page.addressCounty.disabled).toBeFalsy();
    expect(page.addressPostCode.disabled).toBeFalsy();
    expect(page.addressCountrySelect.disabled).toBeFalsy();
  });

  it('should edit details and submit form', () => {
    page.firstNameValue = 'Changed';

    page.submitButton.click();
    fixture.detectChanges();

    expect(updateTargetUnitAccountService.updateTargetUnitAccountAdministrativePerson).toHaveBeenCalledTimes(1);
    expect(updateTargetUnitAccountService.updateTargetUnitAccountAdministrativePerson).toHaveBeenCalledWith(1, {
      ...mockTargetUnitAccountDetails.administrativeContactDetails,
      firstName: 'Changed',
    });
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByLabelText, getByText, setInputValue } from '@testing';

import { mockSectorDetails } from '../../../specs/fixtures/mock';
import { ActiveSectorStore } from '../../active-sector.store';
import { EditSectorAssociationContactDetailsComponent } from './edit-sector-association-contact-details.component';

function generateString(length: number): string {
  let text = '';

  for (let i = 0; i <= length; i++) {
    text = text.concat('a');
  }

  return text;
}

function countFormErrors(message: string): number {
  const inline = Array.from(document.querySelectorAll('.govuk-error-message')).filter((el) =>
    el.textContent?.includes(message),
  ).length;
  const summary = Array.from(document.querySelectorAll('.govuk-error-summary__list a')).filter((el) =>
    el.textContent?.includes(message),
  ).length;

  return inline + summary;
}

describe('EditSectorAssociationContactDetailsComponent', () => {
  let fixture: ComponentFixture<EditSectorAssociationContactDetailsComponent>;
  let store: ActiveSectorStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditSectorAssociationContactDetailsComponent],
      providers: [ActiveSectorStore, provideHttpClient(), provideHttpClientTesting()],
    })
      .overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 1 }) })
      .compileComponents();

    store = TestBed.inject(ActiveSectorStore);
    store.setState(mockSectorDetails);

    fixture = TestBed.createComponent(EditSectorAssociationContactDetailsComponent);
    fixture.detectChanges();
  });

  it('should render title', () => {
    expect(getByText('Change sector contact details')).toBeTruthy();
  });

  it('should contain twelve (12) inputs', () => {
    const inputs = document.querySelectorAll('.govuk-input');
    expect(inputs.length).toBe(12);
  });

  it('should correctly fill input values', () => {
    const title = getByLabelText('Title (optional)');
    expect((title as HTMLInputElement).value).toBe('Mr.');

    const firstName = getByLabelText('First name');
    expect((firstName as HTMLInputElement).value).toBe('John');

    const lastName = getByLabelText('Last name');
    expect((lastName as HTMLInputElement).value).toBe('Doe');

    const jobTitle = getByLabelText('Job title (optional)');
    expect((jobTitle as HTMLInputElement).value).toBe('job title');

    const organisationName = getByLabelText('Organisation name (optional)');
    expect((organisationName as HTMLInputElement).value).toBe('org name');

    const phoneNumber = getByLabelText('Phone number (optional)');
    expect((phoneNumber as HTMLInputElement).value).toBe('123456789');

    const email = getByLabelText('Email address');
    expect((email as HTMLInputElement).value).toBe('johny@doe.com');

    const line1 = getByLabelText('Address line 1');
    expect((line1 as HTMLInputElement).value).toBe('address 1');

    const line2 = getByLabelText('Address line 2 (optional)');
    expect((line2 as HTMLInputElement).value).toBe('');

    const city = getByLabelText('Town or city');
    expect((city as HTMLInputElement).value).toBe('city 1');

    const county = getByLabelText('County (optional)');
    expect((county as HTMLInputElement).value).toBe('');

    const postCode = getByLabelText('Postcode');
    expect((postCode as HTMLInputElement).value).toBe('12345');
  });

  it('should display user details form error(s)', async () => {
    const requiredUserDetailsFields = [
      { label: 'First name', length: 255 },
      { label: 'Last name', length: 255 },
      { label: 'Email address', length: 255 },
    ];

    const submitBtn = getByText('Submit');

    for (const field of requiredUserDetailsFields) {
      const input = getByLabelText(field.label) as HTMLInputElement;
      setInputValue(input, '');
      fixture.detectChanges();
      expect(input.value).toEqual('');

      click(submitBtn);
      fixture.detectChanges();
      expect(countFormErrors(`Enter the ${field.label.toLowerCase()}`)).toBe(2);

      setInputValue(input, generateString(field.length + 1));
      fixture.detectChanges();
      click(submitBtn);
      fixture.detectChanges();
      await fixture.whenStable();

      const errorDesc = countFormErrors(
        `The ${field.label.toLowerCase()} should not be more than ${field.length} characters`,
      );

      expect(errorDesc).toBe(2);
    }
  });

  it('should display address fields form error(s)', async () => {
    const submitBtn = getByText('Submit');

    const requiredAddressFields = [
      { label: 'Address line 1', length: 255 },
      { label: 'Town or city', length: 255 },
      { label: 'Postcode', length: 64 },
    ];

    for (const field of requiredAddressFields) {
      const input = getByLabelText(field.label) as HTMLInputElement;
      setInputValue(input, '');
      fixture.detectChanges();
      expect(input.value).toEqual('');
    }

    click(submitBtn);
    fixture.detectChanges();
    expect(countFormErrors(`Enter address line 1, typically the building and street`)).toBe(2);
    expect(countFormErrors(`Enter a town or city`)).toBe(2);
    expect(countFormErrors(`Enter a postcode`)).toBe(2);

    for (const field of requiredAddressFields) {
      const input = getByLabelText(field.label) as HTMLInputElement;
      setInputValue(input, generateString(field.length + 1));
      fixture.detectChanges();
      click(submitBtn);
      fixture.detectChanges();
    }

    await fixture.whenStable();

    const addressErrorDesc = countFormErrors(`The address should not be more than 255 characters`);
    expect(addressErrorDesc).toBe(2);

    const cityErrorDesc = countFormErrors(`The city should not be more than 255 characters`);
    expect(cityErrorDesc).toBe(2);

    const postcodeErrorDesc = countFormErrors(`The postcode should not be more than 64 characters`);
    expect(postcodeErrorDesc).toBe(2);
  });
});

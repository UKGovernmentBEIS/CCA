import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByLabelText, getByText, setInputValue } from '@testing';

import { mockSectorDetails } from '../../../specs/fixtures/mock';
import { ActiveSectorStore } from '../../active-sector.store';
import { EditSectorAssociationDetailsComponent } from './edit-sector-association-details.component';

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

describe('EditSectorAssociationDetailsComponent', () => {
  let fixture: ComponentFixture<EditSectorAssociationDetailsComponent>;
  let store: ActiveSectorStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditSectorAssociationDetailsComponent],
      providers: [ActiveSectorStore, provideHttpClient(), provideHttpClientTesting()],
    })
      .overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 1 }) })
      .compileComponents();

    store = TestBed.inject(ActiveSectorStore);
    store.setState(mockSectorDetails);

    fixture = TestBed.createComponent(EditSectorAssociationDetailsComponent);
    fixture.detectChanges();
  });

  it('should render title', () => {
    expect(getByText('Change sector details')).toBeTruthy();
  });

  it('should contain seven (7) inputs', () => {
    const inputs = document.querySelectorAll('.govuk-input');
    expect(inputs.length).toBe(7);
  });

  it('should correctly fill input values', () => {
    const sectorName = getByLabelText('Sector name');
    expect((sectorName as HTMLInputElement).value).toBe('common');

    const sectorTradeAssosciationName = getByLabelText('Sector / Trade association name');
    expect((sectorTradeAssosciationName as HTMLInputElement).value).toBe('legal');

    const addressLine1 = getByLabelText('Address line 1');
    expect((addressLine1 as HTMLInputElement).value).toBe('address 1');

    const addressLine2 = getByLabelText('Address line 2 (optional)');
    expect((addressLine2 as HTMLInputElement).value).toBe('');

    const townOrCity = getByLabelText('Town or city');
    expect((townOrCity as HTMLInputElement).value).toBe('city 1');

    const county = getByLabelText('County (optional)');
    expect((county as HTMLInputElement).value).toBe('');

    const postCode = getByLabelText('Postcode');
    expect((postCode as HTMLInputElement).value).toBe('12345');
  });

  it('should display secror fields form error(s)', async () => {
    const submitBtn = getByText('Submit');

    const requiredSectorFields = [
      { label: 'Sector name', length: 255 },
      { label: 'Sector / Trade association name', length: 255 },
    ];

    for (const field of requiredSectorFields) {
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

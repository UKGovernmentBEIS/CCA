import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

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

describe('EditSectorAssociationContactDetailsComponent', () => {
  let store: ActiveSectorStore;

  beforeEach(async () => {
    await render(EditSectorAssociationContactDetailsComponent, {
      providers: [ActiveSectorStore, provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 1 }) });
        store = testbed.inject(ActiveSectorStore);
        store.setState(mockSectorDetails);
      },
    });
  });

  it('should render title', () => {
    expect(screen.getByText('Change sector contact details')).toBeInTheDocument();
  });

  it('should contain twelve (12) inputs', () => {
    const inputs = document.querySelectorAll('.govuk-input');
    expect(inputs.length).toBe(12);
  });

  it('should correctly fill input values', async () => {
    const title = screen.getByLabelText('Title (optional)');
    expect((title as HTMLInputElement).value).toBe('Mr.');

    const firstName = screen.getByLabelText('First name');
    expect((firstName as HTMLInputElement).value).toBe('John');

    const lastName = screen.getByLabelText('Last name');
    expect((lastName as HTMLInputElement).value).toBe('Doe');

    const jobTitle = screen.getByLabelText('Job title (optional)');
    expect((jobTitle as HTMLInputElement).value).toBe('job title');

    const organisationName = screen.getByLabelText('Organisation name (optional)');
    expect((organisationName as HTMLInputElement).value).toBe('org name');

    const phoneNumber = screen.getByLabelText('Phone number (optional)');
    expect((phoneNumber as HTMLInputElement).value).toBe('123456789');

    const email = screen.getByLabelText('Email address');
    expect((email as HTMLInputElement).value).toBe('johny@doe.com');

    const line1 = screen.getByLabelText('Address line 1');
    expect((line1 as HTMLInputElement).value).toBe('address 1');

    const line2 = screen.getByLabelText('Address line 2 (optional)');
    expect((line2 as HTMLInputElement).value).toBe('');

    const city = screen.getByLabelText('Town or city');
    expect((city as HTMLInputElement).value).toBe('city 1');

    const county = screen.getByLabelText('County (optional)');
    expect((county as HTMLInputElement).value).toBe('');

    const postCode = screen.getByLabelText('Postcode');
    expect((postCode as HTMLInputElement).value).toBe('12345');
  });

  it('should display user details form error(s)', async () => {
    const requiredUserDetailsFields = [
      { label: 'First name', length: 255 },
      { label: 'Last name', length: 255 },
      { label: 'Email address', length: 255 },
    ];

    const submitBtn = screen.getByText('Submit');
    const user = UserEvent.setup();

    for (const field of requiredUserDetailsFields) {
      const input = screen.getByLabelText(field.label) as HTMLInputElement;
      await user.clear(input);
      expect(input.value).toEqual('');

      await user.click(submitBtn);
      expect(screen.getAllByText(`Enter the ${field.label.toLowerCase()}`)).toHaveLength(2);

      await user.click(input);
      await user.paste(generateString(field.length + 1));
      await user.click(submitBtn);

      const errorDesc = await screen.findAllByText(
        `The ${field.label.toLowerCase()} should not be more than ${field.length} characters`,
      );

      expect(errorDesc).toHaveLength(2);
    }
  });

  it('should display address fields form error(s)', async () => {
    const submitBtn = screen.getByText('Submit');
    const user = UserEvent.setup();

    const requiredAddressFields = [
      { label: 'Address line 1', length: 255 },
      { label: 'Town or city', length: 255 },
      { label: 'Postcode', length: 64 },
    ];

    for (const field of requiredAddressFields) {
      const input = screen.getByLabelText(field.label) as HTMLInputElement;
      await user.clear(input);
      expect(input.value).toEqual('');
    }

    await user.click(submitBtn);
    expect(screen.getAllByText(`Enter address line 1, typically the building and street`)).toHaveLength(2);
    expect(screen.getAllByText(`Enter a town or city`)).toHaveLength(2);
    expect(screen.getAllByText(`Enter a postcode`)).toHaveLength(2);

    for (const field of requiredAddressFields) {
      const input = screen.getByLabelText(field.label) as HTMLInputElement;
      await user.click(input);
      await user.paste(generateString(field.length + 1));
      await user.click(submitBtn);
    }

    const addressErrorDesc = await screen.findAllByText(`The address should not be more than 255 characters`);
    expect(addressErrorDesc).toHaveLength(2);

    const cityErrorDesc = await screen.findAllByText(`The city should not be more than 255 characters`);
    expect(cityErrorDesc).toHaveLength(2);

    const postcodeErrorDesc = await screen.findAllByText(`The postcode should not be more than 64 characters`);
    expect(postcodeErrorDesc).toHaveLength(2);
  });
});

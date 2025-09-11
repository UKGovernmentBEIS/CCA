import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

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

describe('EditSectorAssociationDetailsComponent', () => {
  let store: ActiveSectorStore;

  beforeEach(async () => {
    await render(EditSectorAssociationDetailsComponent, {
      providers: [ActiveSectorStore, provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 1 }) });
        store = testbed.inject(ActiveSectorStore);
        store.setState(mockSectorDetails);
      },
    });
  });

  it('should render title', () => {
    expect(screen.getByText('Change sector details')).toBeInTheDocument();
  });

  it('should contain seven (7) inputs', () => {
    const inputs = document.querySelectorAll('.govuk-input');
    expect(inputs.length).toBe(7);
  });

  it('should correctly fill input values', async () => {
    const sectorName = screen.getByLabelText('Sector name');
    expect((sectorName as HTMLInputElement).value).toBe('common');

    const sectorTradeAssosciationName = screen.getByLabelText('Sector / Trade association name');
    expect((sectorTradeAssosciationName as HTMLInputElement).value).toBe('legal');

    const addressLine1 = screen.getByLabelText('Address line 1');
    expect((addressLine1 as HTMLInputElement).value).toBe('address 1');

    const addressLine2 = screen.getByLabelText('Address line 2 (optional)');
    expect((addressLine2 as HTMLInputElement).value).toBe('');

    const townOrCity = screen.getByLabelText('Town or city');
    expect((townOrCity as HTMLInputElement).value).toBe('city 1');

    const county = screen.getByLabelText('County (optional)');
    expect((county as HTMLInputElement).value).toBe('');

    const postCode = screen.getByLabelText('Postcode');
    expect((postCode as HTMLInputElement).value).toBe('12345');
  });

  it('should display secror fields form error(s)', async () => {
    const submitBtn = screen.getByText('Submit');
    const user = UserEvent.setup();

    const requiredSectorFields = [
      { label: 'Sector name', length: 255 },
      { label: 'Sector / Trade association name', length: 255 },
    ];

    for (const field of requiredSectorFields) {
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

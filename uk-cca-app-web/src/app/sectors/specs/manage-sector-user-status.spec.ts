import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { click, getByText } from '@testing';

import { SECTORS_ROUTES } from '../sectors.routes';
import { mockSectorAuthorities } from './fixtures/mock';
import { navigateToContacts } from './test.utils';
describe('Manage sector user status spec', () => {
  let httpMock: HttpTestingController;
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideRouter(SECTORS_ROUTES), provideHttpClient(), provideHttpClientTesting()],
    });

    httpMock = TestBed.inject(HttpTestingController);
    await TestBed.compileComponents();
    harness = await RouterTestingHarness.create();
  });

  afterEach(fakeAsync(() => {
    flush();
    httpMock.verify();
  }));

  it('Main: Αctivate a Sector User in ‘Disabled’ status', fakeAsync(async () => {
    const sectorId = 123;
    const opts = { harness, httpTestingController: httpMock };
    await navigateToContacts(sectorId, opts);

    // assert that a disabled user does not have the option to change role
    const lastRow = document.querySelector(
      "govuk-table[formarrayname='authorities'] tbody tr:last-child",
    ) as HTMLElement;

    expect(getByText('Administrator User', lastRow)).toBeTruthy();
    const length = mockSectorAuthorities.authorities.length;
    expect(document.getElementById(`authorities.${length - 1}.userType`)).toBeFalsy();

    // change user status
    const statusSelect = document.getElementById(`authorities.${length - 1}.status`) as HTMLSelectElement;
    statusSelect.value = '0: ACTIVE';
    statusSelect.dispatchEvent(new Event('change', { bubbles: true }));
    harness.detectChanges();

    // assert that a user can change role for active status
    expect(document.getElementById(`authorities.${length - 1}.userType`)).toBeTruthy();

    // update sector user list
    click(getByText('Save'));
    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(null);
    expect(
      (document.getElementById(`authorities.${length - 1}.status`) as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe('0: ACTIVE');
    expect(
      (document.getElementById(`authorities.${length - 1}.userType`) as HTMLInputElement | HTMLSelectElement | null)
        ?.value ?? '',
    ).toBe('0: sector_user_administrator');
  }));

  it('Alternative scenario 1: Disable an active Sector User', fakeAsync(async () => {
    const sectorId = 123;
    const opts = { harness, httpTestingController: httpMock };

    await navigateToContacts(sectorId, opts);
    const activeSectorUserIdx = mockSectorAuthorities.authorities.findIndex((a) => a.status === 'ACTIVE');
    const statusSelect = document.getElementById(`authorities.${activeSectorUserIdx}.status`) as HTMLSelectElement;
    statusSelect.value = '1: DISABLED';
    statusSelect.dispatchEvent(new Event('change', { bubbles: true }));
    harness.detectChanges();

    // assert that a user cannot change role for active status
    expect(document.getElementById(`authorities.${activeSectorUserIdx}.userType`)).toBeFalsy();

    // update sector user list
    click(getByText('Save'));
    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(null);
    expect(
      (
        document.getElementById(`authorities.${activeSectorUserIdx}.status`) as
          | HTMLInputElement
          | HTMLSelectElement
          | null
      )?.value ?? '',
    ).toBe('1: DISABLED');
  }));
});

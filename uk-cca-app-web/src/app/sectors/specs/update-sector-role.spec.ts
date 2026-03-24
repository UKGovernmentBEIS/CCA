import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { click, getByText } from '@testing';

import { SECTORS_ROUTES } from '../sectors.routes';
import { mockSectorAuthorities } from './fixtures/mock';
import { navigateToContacts } from './test.utils';

describe('Update sector user role spec', () => {
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

  it("Main: Update User type from ‘Basic User' to 'Administrator User’", fakeAsync(async () => {
    const sectorId = 231;

    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock });
    expect(
      (document.getElementById('authorities.0.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('1: sector_user_basic_user');

    const typeSelect0 = document.getElementById('authorities.0.userType') as HTMLSelectElement;
    typeSelect0.value = '0: sector_user_administrator';
    typeSelect0.dispatchEvent(new Event('change', { bubbles: true }));
    expect(
      (document.getElementById('authorities.0.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('0: sector_user_administrator');

    click(getByText('Save'));
    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(null);
    expect(
      (document.getElementById('authorities.1.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('0: sector_user_administrator');
  }));

  it('should discard changes', fakeAsync(async () => {
    const sectorId = 231;
    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock });

    expect(
      (document.getElementById('authorities.1.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('0: sector_user_administrator');
    const typeSelect1 = document.getElementById('authorities.1.userType') as HTMLSelectElement;
    typeSelect1.value = '1: sector_user_basic_user';
    typeSelect1.dispatchEvent(new Event('change', { bubbles: true }));
    expect(
      (document.getElementById('authorities.1.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('1: sector_user_basic_user');

    click(getByText('Discard changes'));
    await harness.fixture.whenStable();

    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(JSON.parse(JSON.stringify(mockSectorAuthorities)));
    harness.detectChanges();
    expect(
      (document.getElementById('authorities.1.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('0: sector_user_administrator');
  }));

  it("Alternative scenario 1: Update User type from ‘Administrator User' (not unique Sector Administrator) to 'Basic User’", fakeAsync(async () => {
    const sectorId = 231;

    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock });
    const typeSelect1 = document.getElementById('authorities.1.userType') as HTMLSelectElement;
    typeSelect1.value = '1: sector_user_basic_user';
    typeSelect1.dispatchEvent(new Event('change', { bubbles: true }));
    click(getByText('Save'));

    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(null);
    expect(
      (document.getElementById('authorities.1.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('1: sector_user_basic_user');
  }));

  it("Alternative scenario 2: Update User type from ‘Administrator User' (unique Sector Administrator) to 'Basic User’", fakeAsync(async () => {
    const sectorId = 231;

    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock });
    expect(
      (document.getElementById('authorities.1.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('0: sector_user_administrator');
    expect(
      (document.getElementById('authorities.2.userType') as HTMLInputElement | HTMLSelectElement | null)?.value ?? '',
    ).toBe('0: sector_user_administrator');

    const typeSelect1 = document.getElementById('authorities.1.userType') as HTMLSelectElement;
    const typeSelect2 = document.getElementById('authorities.2.userType') as HTMLSelectElement;
    typeSelect1.value = '1: sector_user_basic_user';
    typeSelect1.dispatchEvent(new Event('change', { bubbles: true }));
    typeSelect2.value = '1: sector_user_basic_user';
    typeSelect2.dispatchEvent(new Event('change', { bubbles: true }));
    click(getByText('Save'));
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(getByText('At least one sector admin should exist in sector association')).toBeTruthy();
  }));
});

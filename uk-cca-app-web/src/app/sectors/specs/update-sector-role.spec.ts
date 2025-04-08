import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

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
    const user = UserEvent.setup();

    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock, user });
    expect(document.getElementById('authorities.0.userType')).toHaveValue('1: sector_user_basic_user');

    await user.selectOptions(document.getElementById('authorities.0.userType'), '0: sector_user_administrator');
    expect(document.getElementById('authorities.0.userType')).toHaveValue('0: sector_user_administrator');

    await user.click(screen.getByText('Save'));
    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(null);
    expect(document.getElementById('authorities.1.userType')).toHaveValue('0: sector_user_administrator');
  }));

  it('should discard changes', fakeAsync(async () => {
    const sectorId = 231;
    const user = UserEvent.setup();
    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock, user });

    expect(document.getElementById('authorities.1.userType')).toHaveValue('0: sector_user_administrator');
    await user.selectOptions(document.getElementById('authorities.1.userType'), '1: sector_user_basic_user');
    expect(document.getElementById('authorities.1.userType')).toHaveValue('1: sector_user_basic_user');

    await user.click(screen.getByText('Discard changes'));
    await harness.fixture.whenStable();

    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(JSON.parse(JSON.stringify(mockSectorAuthorities)));
    harness.detectChanges();
    expect(document.getElementById('authorities.1.userType')).toHaveValue('0: sector_user_administrator');
  }));

  it("Alternative scenario 1: Update User type from ‘Administrator User' (not unique Sector Administrator) to 'Basic User’", fakeAsync(async () => {
    const sectorId = 231;
    const user = UserEvent.setup();

    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock, user });
    await user.selectOptions(document.getElementById('authorities.1.userType'), '1: sector_user_basic_user');
    await user.click(screen.getByText('Save'));

    const req = httpMock.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
    req.flush(null);
    expect(document.getElementById('authorities.1.userType')).toHaveValue('1: sector_user_basic_user');
  }));

  it("Alternative scenario 2: Update User type from ‘Administrator User' (unique Sector Administrator) to 'Basic User’", fakeAsync(async () => {
    const sectorId = 231;
    const user = UserEvent.setup();

    await navigateToContacts(sectorId, { harness, httpTestingController: httpMock, user });
    expect(document.getElementById('authorities.1.userType')).toHaveValue('0: sector_user_administrator');
    expect(document.getElementById('authorities.2.userType')).toHaveValue('0: sector_user_administrator');

    await user.selectOptions(document.getElementById('authorities.1.userType'), '1: sector_user_basic_user');
    await user.selectOptions(document.getElementById('authorities.2.userType'), '1: sector_user_basic_user');
    await user.click(screen.getByText('Save'));
    await harness.fixture.whenStable();
    harness.detectChanges();
    expect(document.querySelector('.govuk-error-summary')).toBeVisible();
    expect(screen.getByText('At least one sector admin should exist in sector association')).toBeVisible();
  }));
});

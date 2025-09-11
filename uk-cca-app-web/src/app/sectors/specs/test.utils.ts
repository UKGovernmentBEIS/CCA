import { Location } from '@angular/common';
import { HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { TestBed, tick } from '@angular/core/testing';
import { RouterTestingHarness } from '@angular/router/testing';

import { screen } from '@testing-library/dom';
import UserEvent, { UserEvent as UE } from '@testing-library/user-event';

import { SectorComponent } from '../sector/sector.component';
import {
  mockOperatorAuthorities,
  mockPhoneCodeCoutries,
  mockSectorAuthorities,
  mockSectorDetails,
  mockTargetUnitAccount,
  mockTargetUnits,
} from './fixtures/mock';
import { sectorBasicUserDetailsFixture } from './fixtures/sector-user-details.fixture';

type Opts = {
  harness: RouterTestingHarness;
  httpTestingController: HttpTestingController;
  user: UE;
};

export async function navigateToAddOperatorUser({ user, harness }: Opts) {
  expect(document.getElementById('users-and-contacts')).toBeVisible;
  harness.detectChanges();

  await user.click(screen.getByText('Add a new operator'));
  expect(screen.getByTestId('add-operator-form')).toBeVisible();
}

export async function navigateToTargetUnitUsers({ user, harness, httpTestingController }: Opts, accountId: number) {
  expect(screen.getByTestId('target-unit')).toBeVisible();
  await user.click(screen.getByText('Users and contacts'));
  harness.detectChanges();

  const req = httpTestingController.expectOne(`/api/v1.0/operator-authorities/account/${accountId}`);
  req.flush(mockOperatorAuthorities);
  expect(document.getElementById('users-and-contacts')).toBeVisible();
}

export async function navigateToTargetUnit(sectorId: number, targetUnitName: string, opts: Opts) {
  const { user, harness, httpTestingController } = opts;
  await navigateToTargetUnits(sectorId, opts);
  await user.click(screen.getByText(targetUnitName));

  const req = httpTestingController.expectOne('/api/v1.0/target-unit-accounts/1');
  req.flush(mockTargetUnitAccount(sectorId));
  await harness.fixture.whenStable();
  harness.detectChanges();
  expect(screen.getByTestId('target-unit')).toBeVisible();
}

export async function navigateToTargetUnits(id: number, { harness, httpTestingController }: Opts) {
  const user = UserEvent.setup();
  harness.navigateByUrl(`/${id}`, SectorComponent);
  let req: TestRequest | null;
  tick();
  req = httpTestingController.expectOne(`/api/v1.0/sector-association/${id}`);
  req.flush(mockSectorDetails);
  await harness.fixture.whenStable();

  await user.click(screen.getByText('Target units'));
  req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
  req.flush(mockSectorAuthorities);
  await harness.fixture.whenStable();

  req = httpTestingController.expectOne(`/api/v1.0/sector-association/${id}/target-unit-accounts/?page=0&size=50`);
  req.flush(mockTargetUnits);
  await harness.fixture.whenStable();

  harness.detectChanges();
  expect(screen.getByTestId('target-unit-list')).toBeVisible();
}

export async function navigateToContacts(id: number, { harness, httpTestingController }: Opts) {
  const user = UserEvent.setup();
  harness.navigateByUrl(`/${id}`, SectorComponent);
  tick();
  let req: TestRequest | null;
  req = httpTestingController.expectOne(`/api/v1.0/sector-association/${id}`);
  req.flush(mockSectorDetails);
  await harness.fixture.whenStable();

  await user.click(screen.getByText('Contacts'));
  req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
  req.flush(mockSectorAuthorities);
  await harness.fixture.whenStable();
  harness.detectChanges();
  expect(screen.getByTestId('sector-user-type-form')).toBeVisible();
}

export async function navigateToAddSector(
  id: number,
  roleValue: string,
  { harness, httpTestingController, user }: Opts,
) {
  await user.selectOptions(document.getElementById('userType'), roleValue);
  await user.click(screen.getByText('Continue'));

  const req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
  req.flush(mockSectorAuthorities);
  await harness.fixture.whenStable();

  expect(screen.getByTestId('add-sector-user-form')).toBeVisible();
  expect(TestBed.inject(Location).path()).toEqual(`/${id}/sector-user/add?role=sector_user_administrator`);
  harness.fixture.detectChanges();
}

export async function fillAddSectorUserForm(email: string, { user }: Opts) {
  await user.type(screen.getByLabelText('Email address'), email);
  await user.type(screen.getByLabelText('First name'), 'Sector');
  await user.type(screen.getByLabelText('Last name'), 'User');
}

export async function submitSectorUserForm({ user, harness }: Opts) {
  await user.click(screen.getByText('Submit'));
  harness.detectChanges();
  await harness.fixture.whenStable();
}

export async function assertConfirmationPage() {
  expect(screen.getByTestId('confirmation-screen')).toBeVisible();
  expect(screen.getByText('Return to: Contacts')).toBeVisible();
}

export async function navigateToSectorUserDetails(
  sectorId: number,
  sectorUserId: string,
  name: string,
  { harness, httpTestingController, user }: Opts,
) {
  await user.click(screen.getByText(name));

  let req = httpTestingController.expectOne(`/api/v1.0/sector-users/sector-association/${sectorId}/${sectorUserId}`);
  req.flush(sectorBasicUserDetailsFixture);

  req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
  req.flush(mockSectorAuthorities);

  await harness.fixture.whenStable();
  harness.detectChanges();

  expect(screen.getByTestId('sector-user-details-list')).toBeVisible();
  expect(screen.getByTestId('sector-user-organisation-details-list')).toBeVisible();
}

export async function navigateToEditSectorUserDetails({ harness, httpTestingController, user }: Opts) {
  const changeLink = screen.getAllByText('Change')[0];
  await user.click(changeLink);
  await harness.fixture.whenStable();
  harness.detectChanges();
  const req = httpTestingController.expectOne('/api/v1.0/data?types=COUNTRIES');
  req.flush(mockPhoneCodeCoutries);

  await harness.fixture.whenStable();
  harness.detectChanges();

  expect(screen.getByText('Change user details')).toBeVisible();
}

export async function changeSectorUserDetailsFormName(
  newFirstName: string,
  newLastName: string,
  { harness, user }: Opts,
) {
  const firstNameInput = screen.getByLabelText('First name') as HTMLInputElement;
  const lastNameInput = screen.getByLabelText('Last name') as HTMLInputElement;
  const contactType = document.getElementsByClassName('govuk-radios__input');

  await user.clear(firstNameInput);
  await user.type(firstNameInput, newFirstName);

  await user.clear(lastNameInput);
  await user.type(lastNameInput, newLastName);

  await user.click(contactType[1]);

  harness.detectChanges();
  await harness.fixture.whenStable();

  expect(firstNameInput).toHaveValue(newFirstName);
  expect(lastNameInput).toHaveValue(newLastName);
  expect(contactType[1]).toBeChecked();
}

export async function submitSectorUserDetailsForm({ user, harness }: Opts) {
  await user.click(screen.getByText('Confirm and continue'));
  await harness.fixture.whenStable();
}

export async function checkThatSectorUserDetailsAreUpdated(newName: string) {
  expect(screen.getByText(newName)).toBeVisible();
}

import { Location } from '@angular/common';
import { HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { TestBed, tick } from '@angular/core/testing';
import { RouterTestingHarness } from '@angular/router/testing';

import { clear, click, getByLabelText, getByTestId, getByText, type } from '@testing';

import { SectorComponent } from '../sector/sector.component';
import {
  mockOperatorAuthorities,
  mockSectorAuthorities,
  mockSectorDetails,
  mockTargetUnitAccount,
  mockTargetUnits,
} from './fixtures/mock';
import { sectorBasicUserDetailsFixture } from './fixtures/sector-user-details.fixture';

type Opts = {
  harness: RouterTestingHarness;
  httpTestingController: HttpTestingController;
};

function normalizedText(value: string | null | undefined): string {
  return (value ?? '').replace(/\s+/g, ' ').trim();
}

function clickInteractiveByText(text: string) {
  const candidates = Array.from(document.querySelectorAll<HTMLElement>('a,button,[role="button"]'));
  const interactive = candidates.find((el) => normalizedText(el.textContent) === text);

  if (interactive) {
    interactive.click();
    return;
  }

  click(getByText(text));
}

function clickTab(tabId: string, label: string) {
  const tabControl =
    (document.querySelector(`a.govuk-tabs__tab[aria-controls="${tabId}"]`) as HTMLElement | null) ??
    (document.querySelector(`button[aria-controls="${tabId}"]`) as HTMLElement | null) ??
    (getByText(label).closest('a,button') as HTMLElement | null) ??
    getByText(label);

  if (tabControl instanceof HTMLAnchorElement || tabControl instanceof HTMLButtonElement) {
    tabControl.click();
  } else {
    click(tabControl);
  }
}

export async function navigateToAddOperatorUser({ harness }: Opts) {
  expect(document.getElementById('users-and-contacts')).toBeTruthy();
  harness.detectChanges();

  clickInteractiveByText('Add a new operator');
  expect(getByTestId('add-operator-form')).toBeTruthy();
}

export async function navigateToTargetUnitUsers({ harness, httpTestingController }: Opts, accountId: number) {
  expect(getByTestId('target-unit')).toBeTruthy();
  clickTab('users-and-contacts', 'Users and contacts');
  harness.detectChanges();
  await harness.fixture.whenStable();

  const req = httpTestingController.expectOne(`/api/v1.0/operator-authorities/account/${accountId}`);
  req.flush(mockOperatorAuthorities);

  expect(document.getElementById('users-and-contacts')).toBeTruthy();
}

export async function navigateToTargetUnit(sectorId: number, targetUnitName: string, opts: Opts) {
  const { harness, httpTestingController } = opts;
  await navigateToTargetUnits(sectorId, opts);
  clickInteractiveByText(targetUnitName);

  const req = httpTestingController.expectOne('/api/v1.0/target-unit-accounts/1');
  req.flush(mockTargetUnitAccount(sectorId));
  await harness.fixture.whenStable();
  harness.detectChanges();
  expect(getByTestId('target-unit-details')).toBeTruthy();
}

export async function navigateToTargetUnits(id: number, { harness, httpTestingController }: Opts) {
  harness.navigateByUrl(`/${id}`, SectorComponent);
  let req: TestRequest | null;
  tick();
  req = httpTestingController.expectOne(`/api/v1.0/sector-association/${id}`);
  req.flush(mockSectorDetails);
  await harness.fixture.whenStable();

  clickTab('target-units', 'Target units');
  harness.detectChanges();
  await harness.fixture.whenStable();
  req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
  req.flush(mockSectorAuthorities);
  await harness.fixture.whenStable();

  req = httpTestingController.expectOne(`/api/v1.0/sector-association/${id}/target-unit-accounts/?page=0&size=50`);
  req.flush(mockTargetUnits);
  await harness.fixture.whenStable();

  harness.detectChanges();
  expect(getByTestId('target-unit-list')).toBeTruthy();
}

export async function navigateToContacts(id: number, { harness, httpTestingController }: Opts) {
  harness.navigateByUrl(`/${id}`, SectorComponent);
  tick();
  let req: TestRequest | null;
  req = httpTestingController.expectOne(`/api/v1.0/sector-association/${id}`);
  req.flush(mockSectorDetails);
  await harness.fixture.whenStable();

  clickTab('contacts', 'Contacts');
  harness.detectChanges();
  await harness.fixture.whenStable();
  req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
  req.flush(mockSectorAuthorities);
  await harness.fixture.whenStable();
  harness.detectChanges();
  expect(getByTestId('sector-user-type-form')).toBeTruthy();
}

export async function navigateToAddSector(id: number, roleValue: string, { harness, httpTestingController }: Opts) {
  const selectElement = document.getElementById('userType') as HTMLSelectElement;
  selectElement.value = roleValue;
  selectElement.dispatchEvent(new Event('change', { bubbles: true }));
  clickInteractiveByText('Continue');

  const req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${id}`);
  req.flush(mockSectorAuthorities);
  await harness.fixture.whenStable();

  expect(getByTestId('add-sector-user-form')).toBeTruthy();
  expect(TestBed.inject(Location).path()).toEqual(`/${id}/sector-user/add?role=sector_user_administrator`);
  harness.fixture.detectChanges();
}

export async function fillAddSectorUserForm(email: string) {
  type(getByLabelText('Email address') as HTMLInputElement, email);
  type(getByLabelText('First name') as HTMLInputElement, 'Sector');
  type(getByLabelText('Last name') as HTMLInputElement, 'User');
}

export async function submitSectorUserForm({ harness }: Opts) {
  clickInteractiveByText('Submit');
  harness.detectChanges();
  await harness.fixture.whenStable();
}

export async function assertConfirmationPage() {
  expect(getByTestId('confirmation-screen')).toBeTruthy();
  expect(getByText('Return to: Contacts')).toBeTruthy();
}

export async function navigateToSectorUserDetails(
  sectorId: number,
  sectorUserId: string,
  name: string,
  { harness, httpTestingController }: Opts,
) {
  clickInteractiveByText(name);

  let req = httpTestingController.expectOne(`/api/v1.0/sector-users/sector-association/${sectorId}/${sectorUserId}`);
  req.flush(sectorBasicUserDetailsFixture);

  req = httpTestingController.expectOne(`/api/v1.0/sector-authorities/sector-association/${sectorId}`);
  req.flush(mockSectorAuthorities);

  await harness.fixture.whenStable();
  harness.detectChanges();

  expect(getByTestId('sector-user-details-list')).toBeTruthy();
  expect(getByTestId('sector-user-organisation-details-list')).toBeTruthy();
}

export async function navigateToEditSectorUserDetails({ harness }: Opts) {
  clickInteractiveByText('Change');
  await harness.fixture.whenStable();
  harness.detectChanges();
  expect(getByText('Change user details')).toBeTruthy();
}

export async function changeSectorUserDetailsFormName(newFirstName: string, newLastName: string, { harness }: Opts) {
  const firstNameInput = getByLabelText('First name') as HTMLInputElement;
  const lastNameInput = getByLabelText('Last name') as HTMLInputElement;
  const contactType = document.getElementsByClassName('govuk-radios__input');

  clear(firstNameInput);
  type(firstNameInput, newFirstName);

  clear(lastNameInput);
  type(lastNameInput, newLastName);

  click(contactType[1] as HTMLElement);

  harness.detectChanges();
  await harness.fixture.whenStable();

  expect(firstNameInput.value).toBe(newFirstName);
  expect(lastNameInput.value).toBe(newLastName);
  expect((contactType[1] as HTMLInputElement).checked).toBe(true);
}

export async function submitSectorUserDetailsForm({ harness }: Opts) {
  clickInteractiveByText('Confirm and continue');
  await harness.fixture.whenStable();
}

export async function checkThatSectorUserDetailsAreUpdated(newName: string) {
  expect(getByText(newName)).toBeTruthy();
}

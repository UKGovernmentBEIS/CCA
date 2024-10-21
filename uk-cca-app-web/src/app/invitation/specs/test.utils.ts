import { HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingHarness } from '@angular/router/testing';

import { screen } from '@testing-library/dom';
import { UserEvent } from '@testing-library/user-event';

import { mockPhoneCodeCoutries } from 'src/app/sectors/specs/fixtures/mock';

import { SectorUserInvitationComponent } from '../sector-user-invitation/sector-user-invitation.component';
import { InvitedSectorUserExtended } from '../sector-user-invitation/sector-user-invitation.store';

type Opts = {
  harness: RouterTestingHarness;
  httpTestingController: HttpTestingController;
  user: UserEvent;
};

export async function navigateAndFillSectorUserInvitationDetails(
  sectorUserStoreState: InvitedSectorUserExtended,
  opts: Opts,
) {
  opts.harness.navigateByUrl(
    `/invitation/sector-user?token=${sectorUserStoreState.emailToken}`,
    SectorUserInvitationComponent,
  );
  await opts.harness.fixture.whenStable();

  let req = opts.httpTestingController.expectOne('/api/v1.0/sector-users/registration/accept-invitation');
  req.flush(sectorUserStoreState);
  await opts.harness.fixture.whenStable();

  req = opts.httpTestingController.expectOne('/api/v1.0/data?types=COUNTRIES');
  req.flush(mockPhoneCodeCoutries);
  await opts.harness.fixture.whenStable();
  opts.harness.detectChanges();

  expect(screen.getByTestId('invited-sector-user-details-form')).toBeVisible();
  expect(screen.getByLabelText('First name')).toHaveValue(sectorUserStoreState.firstName);
  expect(screen.getByLabelText('Last name')).toHaveValue(sectorUserStoreState.lastName);
  expect(screen.getByLabelText('Consultant')).toBeChecked();
  expect(screen.getByLabelText('Email address')).toHaveValue(sectorUserStoreState.email);

  await opts.user.click(screen.getByText('Continue'));
  await opts.harness.fixture.whenStable();
  opts.harness.detectChanges();
}

export async function fillPassword(password: string, passwordReenter: string, opts: Opts) {
  await opts.user.type(screen.getByLabelText('Create a password to activate your account'), password);
  await opts.user.type(screen.getByLabelText('Re-enter your password'), passwordReenter);

  await opts.user.click(screen.getByText('Continue'));
  await opts.harness.fixture.whenStable();
  opts.harness.detectChanges();
}

export async function checkUserDetailsAndSubmit(sectorUserStoreState: InvitedSectorUserExtended, opts: Opts) {
  expect(screen.getByTestId('sector-user-invitation-details-list')).toBeVisible();
  expect(screen.getByTestId('sector-user-invitation-organisation-details-list')).toBeVisible();
  expect(screen.getByTestId('sector-user-invitation-password')).toBeVisible();

  await opts.user.click(screen.getByText('Submit'));
  await opts.harness.fixture.whenStable();
  opts.harness.detectChanges();

  const req = opts.httpTestingController.expectOne(
    '/api/v1.0/sector-users/registration/accept-authority-and-enable-invited-sector-user-with-credentials',
  );
  req.flush(sectorUserStoreState);
  await opts.harness.fixture.whenStable();
  opts.harness.detectChanges();
}

export async function navigateToExistingSectorUserInvitationConfirmation(
  sectorUserStoreState: InvitedSectorUserExtended,
  opts: Opts,
) {
  opts.harness.navigateByUrl(
    `/invitation/sector-user?token=${sectorUserStoreState.emailToken}`,
    SectorUserInvitationComponent,
  );
  await opts.harness.fixture.whenStable();

  const req = opts.httpTestingController.expectOne('/api/v1.0/sector-users/registration/accept-invitation');
  req.flush({ ...sectorUserStoreState, invitationStatus: 'ACCEPTED' });
  await opts.harness.fixture.whenStable();
  opts.harness.detectChanges();
}

export async function navigateToExpiredOrInvalidLink(
  sectorUserStoreState: InvitedSectorUserExtended,
  errorRes: { code: string; message: string; security: boolean; data: any[] },
  opts: Opts,
) {
  opts.harness.navigateByUrl(
    `/invitation/sector-user?token=${sectorUserStoreState.emailToken}`,
    SectorUserInvitationComponent,
  );
  await opts.harness.fixture.whenStable();

  const req = opts.httpTestingController.expectOne('/api/v1.0/sector-users/registration/accept-invitation');
  req.flush(errorRes, { status: 400, statusText: '' });

  await opts.harness.fixture.whenStable();
  opts.harness.detectChanges();
}

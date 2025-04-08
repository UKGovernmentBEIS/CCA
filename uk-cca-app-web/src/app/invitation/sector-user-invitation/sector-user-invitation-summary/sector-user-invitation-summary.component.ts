import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';
import { SummaryComponent, SummaryData, SummaryFactory } from '@shared/components';
import { ContactTypeEnum } from '@shared/pipes';
import { transformPhoneNumber } from '@shared/utils';

import { SectorUserRegistrationWithCredentialsDTO, SectorUsersRegistrationService } from 'cca-api';

import { InvitedSectorUserExtended, SectorUserInvitationStore } from '../sector-user-invitation.store';

function toSummaryData(user: InvitedSectorUserExtended): SummaryData {
  return new SummaryFactory()
    .addSection('Your Details', '../', { testid: 'sector-user-invitation-details-list' })
    .addChangeRow('First name', user.firstName)
    .addChangeRow('Last name', user.lastName)
    .addChangeRow('Job title', user.jobTitle)
    .addRow('Email address', user.email)

    .addSection('Your organization details', '../', { testid: 'sector-user-invitation-organisation-details-list' })
    .addChangeRow('Contact type', ContactTypeEnum[user.contactType])
    .addChangeRow('Organisation name', user.organisationName)
    .addChangeRow('Phone number 1', transformPhoneNumber(user.phoneNumber))
    .addChangeRow('Phone number 2', transformPhoneNumber(user.mobileNumber))

    .addSection('', '../create-password', { testid: 'sector-user-invitation-password' })
    .addChangeRow('Password', '')
    .create();
}

@Component({
  selector: 'cca-sector-user-invitation-summary',
  templateUrl: './sector-user-invitation-summary.component.html',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorUserInvitationSummaryComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly sectorUsersRegistrationService = inject(SectorUsersRegistrationService);
  private readonly store = inject(SectorUserInvitationStore);

  protected readonly storeUser = this.store.state;

  readonly summaryData = toSummaryData(this.storeUser);

  onSaveUserInvitationDetails() {
    const sectorUser: SectorUserRegistrationWithCredentialsDTO = {
      emailToken: this.storeUser.emailToken,
      firstName: this.storeUser.firstName,
      lastName: this.storeUser.lastName,
      jobTitle: this.storeUser.jobTitle,
      contactType: this.storeUser.contactType,
      organisationName: this.storeUser.organisationName,
      phoneNumber: this.storeUser.phoneNumber,
      mobileNumber: this.storeUser.mobileNumber,
      password: this.storeUser.password,
    };

    this.sectorUsersRegistrationService
      .acceptAuthorityAndEnableInvitedUserWithCredentials(sectorUser)
      .pipe(
        catchBadRequest([ErrorCodes.EMAIL1001, ErrorCodes.TOKEN1001, ErrorCodes.USER1004], (res) =>
          of({ url: '../invalid-link', queryParams: { code: res.error.code } }),
        ),
      )
      .subscribe(() => {
        this.router.navigate(['..', 'confirmed'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}

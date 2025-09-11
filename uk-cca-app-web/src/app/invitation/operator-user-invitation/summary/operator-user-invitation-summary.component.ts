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

import { CcaOperatorUserRegistrationWithCredentialsDTO, OperatorUsersRegistrationService } from 'cca-api';

import { InvitedOperatorUserExtended, OperatorUserInvitationStore } from '../store';

function toSummaryData(user: InvitedOperatorUserExtended): SummaryData {
  return new SummaryFactory()
    .addSection('Your Details', '../', { testid: 'operator-user-invitation-details-list' })
    .addChangeRow('First name', user.firstName)
    .addChangeRow('Last name', user.lastName)
    .addChangeRow('Job title', user.jobTitle)
    .addChangeRow('Email address', user.email)
    .addSection('Your organization details', '../', { testid: 'operator-user-invitation-organisation-details-list' })
    .addChangeRow('Contact type', ContactTypeEnum[user.contactType])
    .addChangeRow('Organisation name', user.organisationName)
    .addChangeRow('Phone number 1', transformPhoneNumber(user.phoneNumber))
    .addChangeRow('Phone number 2', transformPhoneNumber(user.mobileNumber))
    .addSection('', '../create-password', { testid: 'operator-user-invitation-password' })
    .addChangeRow('Password', '')
    .create();
}

@Component({
  selector: 'cca-sector-user-invitation-summary',
  template: `
    <netz-page-heading [caption]="'Create user account'">Check your answers</netz-page-heading>
    <cca-summary [data]="summaryData" />
    <button netzPendingButton govukButton type="button" (click)="onSaveUserInvitationDetails()">Submit</button>
  `,
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorUserInvitationSummaryComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly operatorUsersRegistrationService = inject(OperatorUsersRegistrationService);
  private readonly store = inject(OperatorUserInvitationStore);

  protected readonly storeUser = this.store.state;

  protected readonly summaryData = toSummaryData(this.storeUser);

  onSaveUserInvitationDetails() {
    const operatorUser: CcaOperatorUserRegistrationWithCredentialsDTO = {
      emailToken: this.storeUser.emailToken,
      firstName: this.storeUser.firstName,
      lastName: this.storeUser.lastName,
      jobTitle: this.storeUser.jobTitle,
      contactType: this.storeUser.contactType as CcaOperatorUserRegistrationWithCredentialsDTO['contactType'],
      organisationName: this.storeUser.organisationName,
      phoneNumber: this.storeUser.phoneNumber,
      mobileNumber: this.storeUser.mobileNumber,
      password: this.storeUser.password,
    };

    this.operatorUsersRegistrationService
      .acceptAuthorityAndEnableInvitedOperatorUserWithCredentials(operatorUser)
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

import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, MonoTypeOperatorFunction, pipe, switchMap, withLatestFrom } from 'rxjs';

import { UserInputSummaryTemplateComponent } from '@shared/components/user-input-summary/user-input-summary.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SummaryHeaderComponent } from '@shared/summary-header/summary-header.component';
import cleanDeep from 'clean-deep';

import { ButtonDirective } from 'govuk-components';

import {
  OperatorInvitedUserInfoDTO,
  OperatorUserRegistrationWithCredentialsDTO,
  OperatorUsersRegistrationService,
} from 'cca-api';

import { UserRegistrationStore } from '../store/user-registration.store';

@Component({
  selector: 'cca-summary',
  templateUrl: './summary.component.html',
  standalone: true,
  imports: [
    AsyncPipe,
    ButtonDirective,
    NgIf,
    PageHeadingComponent,
    PipesModule,
    SummaryHeaderComponent,
    UserInputSummaryTemplateComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  userInfo$ = this.store.select('userRegistrationDTO');
  invitationStatus$ = this.store.select('invitationStatus');

  isSubmitDisabled: boolean;

  constructor(
    private readonly store: UserRegistrationStore,
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  registerUser(): void {
    this.isSubmitDisabled = true;
    const isNoPasswordInvitation = this.store.getState().invitationStatus === 'PENDING_USER_REGISTRATION_NO_PASSWORD';

    combineLatest([this.store.select('userRegistrationDTO'), this.store.select('password'), this.store.select('token')])
      .pipe(
        first(),
        map(([user, password, emailToken]) => cleanDeep({ ...user, password, emailToken })),
        withLatestFrom(this.store.select('isInvited')),
        switchMap(([user, isInvited]: [OperatorUserRegistrationWithCredentialsDTO, boolean]) =>
          isNoPasswordInvitation
            ? this.operatorUsersRegistrationService
                .registerNewUserFromInvitation(user)
                .pipe(this.acceptInvitation(user))
            : isInvited
              ? this.operatorUsersRegistrationService
                  .registerNewUserFromInvitationWithCredentials(user)
                  .pipe(this.acceptInvitation(user))
              : this.operatorUsersRegistrationService.registerUser(user),
        ),
      )
      .subscribe(() =>
        this.router.navigate([isNoPasswordInvitation ? '../../invitation' : '../success'], { relativeTo: this.route }),
      );
  }

  private acceptInvitation(
    user: OperatorUserRegistrationWithCredentialsDTO,
  ): MonoTypeOperatorFunction<OperatorInvitedUserInfoDTO> {
    return pipe(
      switchMap(() =>
        this.operatorUsersRegistrationService.acceptOperatorInvitation({
          token: user.emailToken,
        }),
      ),
    );
  }
}

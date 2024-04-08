import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { ErrorSummaryComponent } from 'govuk-components';

import { InvitedUserInfoDTO, RegulatorUsersRegistrationService } from 'cca-api';

import { PasswordComponent } from 'src/app/shared-user/password/password.component';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { catchBadRequest, ErrorCodes } from '../../error/business-errors';
import { PASSWORD_FORM, passwordFormFactory } from '../../shared-user/password/password-form.factory';

@Component({
  selector: 'cca-regulator-invitation',
  templateUrl: './regulator-invitation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [passwordFormFactory, DestroySubject],
  standalone: true,
  imports: [AsyncPipe, PageHeadingComponent, PasswordComponent, ErrorSummaryComponent, ReactiveFormsModule],
})
export class RegulatorInvitationComponent implements OnInit {
  isSummaryDisplayed = new BehaviorSubject(false);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly regulatorUsersRegistrationService: RegulatorUsersRegistrationService,
    private readonly destroy$: DestroySubject,
    @Inject(PASSWORD_FORM) readonly form: UntypedFormGroup,
  ) {}

  ngOnInit(): void {
    (this.route.data as Observable<{ invitedUser: InvitedUserInfoDTO }>)
      .pipe(takeUntil(this.destroy$))
      .subscribe(({ invitedUser: { email } }) => this.form.patchValue({ email }));
  }

  submitPassword(): void {
    if (this.form.valid) {
      this.route.queryParamMap
        .pipe(
          map((paramMap) => paramMap.get('token')),
          first(),
          switchMap((invitationToken) =>
            this.regulatorUsersRegistrationService.enableRegulatorInvitedUser({
              invitationToken,
              password: this.form.get('password').value,
            }),
          ),
          map(() => ({ url: 'confirmed' })),
          catchBadRequest([ErrorCodes.EMAIL1001, ErrorCodes.TOKEN1001, ErrorCodes.USER1004], (res) =>
            of({ url: 'invalid-link', queryParams: { code: res.error.code } }),
          ),
        )
        .subscribe(({ queryParams, url }: { url: string; queryParams?: any }) =>
          this.router.navigate([url], { relativeTo: this.route, queryParams }),
        );
    } else {
      this.isSummaryDisplayed.next(true);
    }
  }
}

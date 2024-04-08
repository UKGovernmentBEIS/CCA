import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { createCountyAddressControl } from '@shared/county-address-input/create-county-address-controls';
import { phoneInputValidators } from '@shared/phone-input/phone-input.validators';

import { GovukValidators } from 'govuk-components';

import { OperatorUsersRegistrationService } from 'cca-api';

import { UserRegistrationStore } from '../store/user-registration.store';
@Component({
  selector: 'cca-contact-details',
  templateUrl: './contact-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ContactDetailsComponent implements OnInit {
  isSummaryDisplayed = false;
  form: UntypedFormGroup = this.fb.group({
    firstName: [
      null,
      [
        GovukValidators.required('Enter your first name'),
        GovukValidators.maxLength(255, 'Your first name should not be larger than 255 characters'),
      ],
    ],
    lastName: [
      null,
      [
        GovukValidators.required('Enter your last name'),
        GovukValidators.maxLength(255, 'Your last name should not be larger than 255 characters'),
      ],
    ],
    jobTitle: [
      null,
      [
        GovukValidators.required('Enter your job title'),
        GovukValidators.maxLength(255, 'Your job title should not be larger than 255 characters'),
      ],
    ],
    phoneNumber: [
      { countryCode: '44', number: null },
      [GovukValidators.empty('Enter your phone number'), ...phoneInputValidators],
    ],
    mobileNumber: [null, phoneInputValidators],
    email: [{ value: null, disabled: true }],
    address: this.fb.group(createCountyAddressControl(null)),
  });

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: UserRegistrationStore,
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly fb: UntypedFormBuilder,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    combineLatest([this.store.select('userRegistrationDTO'), this.store.select('email')])
      .pipe(
        takeUntil(this.destroy$),
        tap(([, email]) => this.form.patchValue({ email })),
        filter(([user]) => !!user),
        tap(([user]) => this.form.patchValue({ ...user })),
      )
      .subscribe();
  }

  submitContactDetails(): void {
    if (this.form.valid) {
      const { ...model } = this.form.value;
      this.store.setState({ ...this.store.getState(), userRegistrationDTO: model });

      this.router.navigate(
        [
          this.store.getState().isSummarized ||
          this.store.getState().invitationStatus === 'PENDING_USER_REGISTRATION_NO_PASSWORD'
            ? '../summary'
            : '../choose-password',
        ],
        {
          relativeTo: this.route,
        },
      );
    } else {
      this.isSummaryDisplayed = true;
    }
  }
}

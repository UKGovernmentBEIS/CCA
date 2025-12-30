import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { catchError, of, take } from 'rxjs';

import {
  ConditionalContentDirective,
  DetailsComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { sameCompanyRegistrationNumbers } from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { AccountAddressDTO, CompaniesInformationService, CompanyProfileDTO, TargetUnitAccountPayload } from 'cca-api';

import { CompanyRegistrationNumberFormModel } from '../../common/types';
import { CreateTargetUnitStore } from '../create-target-unit.store';
import {
  COMPANY_REGISTRATION_NUMBER_FORM,
  CompanyRegistrationNumberFormProvider,
} from './company-registration-number-form.provider';

type CompanyNumberState = {
  isCompanyRegistrationNumber: boolean;
  companyRegistrationNumber: string;
  registrationNumberMissingReason: string;
};

@Component({
  selector: 'cca-company-registration-number',
  templateUrl: './company-registration-number.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    WizardStepComponent,
    RadioComponent,
    RadioOptionComponent,
    TextInputComponent,
    ConditionalContentDirective,
    DetailsComponent,
  ],
  providers: [CompanyRegistrationNumberFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompanyRegistrationNumberComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly createTargetUnitStore = inject(CreateTargetUnitStore);
  private readonly companiesInformationService = inject(CompaniesInformationService);

  protected readonly form = inject<FormGroup<CompanyRegistrationNumberFormModel>>(COMPANY_REGISTRATION_NUMBER_FORM);

  onSubmitCompanyRegistrationNumber() {
    const registrationNumberState: CompanyNumberState = {
      isCompanyRegistrationNumber: this.form.value.isCompanyRegistrationNumber,
      companyRegistrationNumber: this.form.value.companyRegistrationNumber,
      registrationNumberMissingReason: this.form.value.registrationNumberMissingReason,
    };

    if (this.form.value.isCompanyRegistrationNumber) {
      this.companiesInformationService
        .getCompanyProfileByRegistrationNumber(this.form.value.companyRegistrationNumber)
        .pipe(
          take(1),
          catchError(() => of(null)),
        )
        .subscribe((companyProfile) => {
          this.updateStateAndNavigate(registrationNumberState, companyProfile);
        });
    } else {
      this.updateStateAndNavigate(registrationNumberState, null);
    }
  }

  private updateStateAndNavigate(
    companyNumberState: CompanyNumberState,
    companyProfile: CompanyProfileDTO | null,
  ): void {
    const currentState = this.createTargetUnitStore.state;
    const sameCRN = sameCompanyRegistrationNumbers(companyProfile, currentState.companyRegistrationNumber);

    if (!sameCRN) {
      const updatedState = updateTargetUnitState(currentState, companyNumberState, companyProfile);
      this.createTargetUnitStore.updateState(updatedState);
    }

    this.router.navigate(['..', 'target-unit-details'], { relativeTo: this.activatedRoute });
  }
}

function updateTargetUnitState(
  state: TargetUnitAccountPayload,
  companyNumberState: CompanyNumberState,
  companyProfile: CompanyProfileDTO | null,
): TargetUnitAccountPayload {
  const address: AccountAddressDTO = { ...companyProfile?.address, country: null };

  return {
    ...state,
    companyRegistrationNumber: companyProfile?.registrationNumber ?? companyNumberState.companyRegistrationNumber,
    isCompanyRegistrationNumber: companyNumberState.isCompanyRegistrationNumber,
    registrationNumberMissingReason: companyNumberState.registrationNumberMissingReason ?? null,
    name: companyProfile?.name ?? null,
    operatorType: null,
    sicCodes: companyProfile?.sicCodes ?? [],
    address: address ?? null,
  };
}

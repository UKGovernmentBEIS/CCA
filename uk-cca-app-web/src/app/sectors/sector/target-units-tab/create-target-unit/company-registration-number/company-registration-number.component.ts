import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { catchError, of } from 'rxjs';

import {
  ConditionalContentDirective,
  DetailsComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { AccountAddressDTO, CompaniesInformationService } from 'cca-api';

import { CompanyRegistrationNumberFormModel } from '../../common/types';
import { CreateTargetUnitStore } from '../create-target-unit.store';
import {
  COMPANY_REGISTRATION_NUMBER_FORM,
  CompanyRegistrationNumberFormProvider,
} from './company-registration-number-form.provider';

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
    const state = this.createTargetUnitStore.state;

    const registrationNumberState = {
      isCompanyRegistrationNumber: this.form.value.isCompanyRegistrationNumber,
      companyRegistrationNumber: this.form.value.companyRegistrationNumber,
      registrationNumberMissingReason: this.form.value.registrationNumberMissingReason,
    };

    if (this.form.value.isCompanyRegistrationNumber) {
      this.companiesInformationService
        .getCompanyProfileByRegistrationNumber(this.form.value.companyRegistrationNumber)
        .pipe(catchError(() => of(null)))
        .subscribe((companyProfile) => {
          if (companyProfile) {
            // TODO: We need to clear the country field for now, as the mapping from country code to string
            // and vice versa does not exist yet.
            const address: AccountAddressDTO = { ...companyProfile?.address, country: null };

            this.createTargetUnitStore.updateState({
              ...state,
              isCompanyRegistrationNumber: this.form.value.isCompanyRegistrationNumber,
              companyRegistrationNumber: companyProfile.registrationNumber,
              registrationNumberMissingReason: null,
              name: companyProfile.name ?? null,
              operatorType: null,
              sicCodes: companyProfile.sicCodes ?? [],
              address: address ?? null,
            });
          } else {
            this.createTargetUnitStore.updateState({
              ...registrationNumberState,
              address: null,
              name: null,
              operatorType: null,
              sicCodes: [],
            });
          }

          this.router.navigate(['..', 'target-unit-details'], { relativeTo: this.activatedRoute });
        });
    } else {
      this.createTargetUnitStore.updateState({
        ...registrationNumberState,
        address: null,
        name: null,
        operatorType: null,
        sicCodes: [],
      });

      this.router.navigate(['..', 'target-unit-details'], { relativeTo: this.activatedRoute });
    }
  }
}

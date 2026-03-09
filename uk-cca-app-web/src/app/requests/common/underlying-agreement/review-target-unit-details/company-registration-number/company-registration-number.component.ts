import { ChangeDetectionStrategy, Component, inject, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { catchError, of, take } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import {
  ConditionalContentDirective,
  DetailsComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { CompaniesInformationService } from 'cca-api';

import {
  COMPANY_REGISTRATION_NUMBER_FORM,
  CompanyRegistrationNumberFormModel,
  CompanyRegistrationNumberFormProvider,
} from '../../target-unit-details/company-registration-number-form.provider';
import { CompanyNumberState, CompanyRegistrationNumberSubmitEvent } from '../../types';

@Component({
  selector: 'cca-common-company-registration-number',
  templateUrl: './company-registration-number.component.html',
  imports: [
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
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
export class CommonCompanyRegistrationNumberComponent {
  private readonly companiesInformationService = inject(CompaniesInformationService);

  protected readonly form = inject<FormGroup<CompanyRegistrationNumberFormModel>>(COMPANY_REGISTRATION_NUMBER_FORM);

  protected readonly submitted = output<CompanyRegistrationNumberSubmitEvent>();

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
          this.submitted.emit({ companyNumberState: registrationNumberState, companyProfile });
        });
    } else {
      this.submitted.emit({ companyNumberState: registrationNumberState, companyProfile: null });
    }
  }
}

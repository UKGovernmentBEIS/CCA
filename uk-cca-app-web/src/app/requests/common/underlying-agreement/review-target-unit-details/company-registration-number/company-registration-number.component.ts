import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { catchError, of, take } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  DetailsComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { CompaniesInformationService } from 'cca-api';

import { underlyingAgreementQuery } from '../../+state';
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
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<FormGroup<CompanyRegistrationNumberFormModel>>(COMPANY_REGISTRATION_NUMBER_FORM);

  protected readonly submitted = output<CompanyRegistrationNumberSubmitEvent>();

  private readonly tuDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  );

  protected readonly caption = computed(() => (this.tuDetails() ? 'Change' : 'New target unit'));

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

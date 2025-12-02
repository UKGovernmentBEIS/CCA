import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { catchError, of, take } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  DetailsComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  COMPANY_REGISTRATION_NUMBER_FORM,
  CompanyRegistrationNumberFormModel,
  CompanyRegistrationNumberFormProvider,
  filterFieldsWithFalsyValues,
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  sameCompanyRegistrationNumbers,
  TasksApiService,
  transformAccountReferenceData,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  AccountAddressDTO,
  CompaniesInformationService,
  CompanyProfileDTO,
  UnderlyingAgreementVariationReviewRequestTaskPayload,
  UnderlyingAgreementVariationReviewSavePayload,
} from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { applySaveActionSideEffects, deleteDecision } from '../../../utils';

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
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
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
          this.updateTUDetailsAndNavigate(registrationNumberState, companyProfile);
        });
    } else {
      this.updateTUDetailsAndNavigate(registrationNumberState, null);
    }
  }

  private updateTUDetailsAndNavigate(
    companyNumberState: CompanyNumberState,
    companyProfile: CompanyProfileDTO | null,
  ): void {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalAccountReferenceData = (
      this.requestTaskStore.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.accountReferenceData;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const sameCRN = sameCompanyRegistrationNumbers(
      companyProfile,
      actionPayload?.underlyingAgreementTargetUnitDetails.companyRegistrationNumber,
    );

    const updatedPayload = sameCRN ? actionPayload : updateTUDetails(actionPayload, companyNumberState, companyProfile);

    const originalTUDetails = transformAccountReferenceData(originalAccountReferenceData);
    const currentTUDetails = updatedPayload.underlyingAgreementTargetUnitDetails;

    const areIdentical = areEntitiesIdentical(
      filterFieldsWithFalsyValues(currentTUDetails),
      filterFieldsWithFalsyValues(originalTUDetails),
    );

    const currentDecisions = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)();
    const decisions = areIdentical ? currentDecisions : deleteDecision(currentDecisions, 'TARGET_UNIT_DETAILS');

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: decisions,
      facilitiesReviewGroupDecisions: this.requestTaskStore.select(
        underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
      )(),
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationReviewRequestTaskPayload) => {
        let path = '';

        if (areIdentical) {
          path = '../check-your-answers';
        } else {
          path = isTargetUnitDetailsWizardCompleted(payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails)
            ? '../decision'
            : `../${ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS}`;
        }

        this.router.navigate([path], { relativeTo: this.activatedRoute });
      });
  }
}

function updateTUDetails(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  companyNumberState: CompanyNumberState,
  companyProfile: CompanyProfileDTO | null,
): UnderlyingAgreementVariationReviewSavePayload {
  // TODO: We need to clear the country field for now, as the mapping from country code to string
  // and vice versa does not exist yet.
  const address: AccountAddressDTO = { ...companyProfile?.address, country: null };

  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails = {
      ...draft.underlyingAgreementTargetUnitDetails,
      companyRegistrationNumber: companyProfile?.registrationNumber ?? companyNumberState.companyRegistrationNumber,
      isCompanyRegistrationNumber: companyNumberState.isCompanyRegistrationNumber,
      registrationNumberMissingReason: companyNumberState.registrationNumberMissingReason,
      operatorName: companyProfile?.name ?? null,
      operatorType: null,
      operatorAddress: address ?? null,
      subsectorAssociationId: null,
      subsectorAssociationName: null,
      responsiblePersonDetails: {
        ...draft.underlyingAgreementTargetUnitDetails.responsiblePersonDetails,
        address: null,
      },
    };
  });
}

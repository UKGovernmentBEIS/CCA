import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  GovukSelectOption,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
} from '@netz/govuk-components';
import {
  AgreementTypeEnum,
  CaNameEnum,
  FACILITY_ELIGIBILITY_DETAILS_FORM,
  FacilityEligibilityDetailsFormProvider,
  FacilityEligibilityFormModel,
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { FileInputComponent, TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  EligibilityDetailsAndAuthorisation,
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../transform';
import { extractReviewProps } from '../../../../utils';
@Component({
  selector: 'cca-facility-eligibility-details',
  templateUrl: './facility-eligibility-details.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    TextInputComponent,
    SelectComponent,
    FileInputComponent,
    RouterLink,
  ],
  providers: [FacilityEligibilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityEligibilityDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<FormGroup<FacilityEligibilityFormModel>>(FACILITY_ELIGIBILITY_DETAILS_FORM);

  private readonly facilityId = this.route.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly agreementTypeOptions: GovukSelectOption<EligibilityDetailsAndAuthorisation['agreementType']>[] = [
    {
      value: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
      text: AgreementTypeEnum['ENVIRONMENTAL_PERMITTING_REGULATIONS'],
    },
    {
      value: 'ENERGY_INTENSIVE',
      text: AgreementTypeEnum['ENERGY_INTENSIVE'],
    },
  ];

  protected readonly caOptions: GovukSelectOption<EligibilityDetailsAndAuthorisation['regulatorName']>[] = [
    {
      value: 'ENVIRONMENT_AGENCY',
      text: CaNameEnum['ENVIRONMENT_AGENCY'],
    },
    {
      value: 'SCOTTISH_ENVIRONMENT_PROTECTION_AGENCY',
      text: CaNameEnum['SCOTTISH_ENVIRONMENT_PROTECTION_AGENCY'],
    },
    {
      value: 'DEPARTMENT_OF_AGRICULTURE_ENVIRONMENT_AND_RURAL_AFFAIRS',
      text: CaNameEnum['DEPARTMENT_OF_AGRICULTURE_ENVIRONMENT_AND_RURAL_AFFAIRS'],
    },
    {
      value: 'NATURAL_RESOURCES_WALES',
      text: CaNameEnum['NATURAL_RESOURCES_WALES'],
    },
    {
      value: 'OTHER',
      text: CaNameEnum['OTHER'],
    },
  ];

  private readonly agreementType: Signal<EligibilityDetailsAndAuthorisation['agreementType']> = toSignal(
    this.form.get('agreementType').valueChanges,
    {
      initialValue: this.form.get('agreementType').value,
    },
  );

  protected readonly isEnvironmental: Signal<boolean> = computed(() => {
    const agreementType = this.agreementType();

    if (agreementType === 'ENVIRONMENTAL_PERMITTING_REGULATIONS') {
      this.form.get('erpAuthorisationExists').enable();
      return true;
    } else {
      this.form.get('erpAuthorisationExists').disable();
      this.form.get('authorisationNumber').disable();
      this.form.get('regulatorName').disable();
      this.form.get('permitFile').disable();

      this.form.get('erpAuthorisationExists').reset();
      this.form.get('authorisationNumber').reset();
      this.form.get('regulatorName').reset();
      this.form.get('permitFile').reset();
      return false;
    }
  });

  private readonly erpAuthorisationExists: Signal<EligibilityDetailsAndAuthorisation['erpAuthorisationExists']> =
    toSignal(this.form.get('erpAuthorisationExists').valueChanges, {
      initialValue: this.form.get('erpAuthorisationExists').value,
    });

  protected readonly isAuthorisation: Signal<boolean> = computed(() => {
    if (this.erpAuthorisationExists()) {
      this.form.get('authorisationNumber').enable();
      this.form.get('regulatorName').enable();
      this.form.get('permitFile').enable();
      return true;
    } else {
      this.form.get('authorisationNumber').disable();
      this.form.get('regulatorName').disable();
      this.form.get('permitFile').disable();

      this.form.get('authorisationNumber').reset();
      this.form.get('regulatorName').reset();
      this.form.get('permitFile').reset();
      return false;
    }
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const updatedPayload = updateFacilityEligibilityDetails(actionPayload, this.form, this.facilityId);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)() as number;
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationSubmitRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        if (isFacilityWizardCompleted(facility)) {
          this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
        } else {
          this.router.navigate([`../${FacilityWizardStep.EXTENT}`], { relativeTo: this.route });
        }
      });
  }
}

function updateFacilityEligibilityDetails(
  payload: UnderlyingAgreementVariationApplySavePayload,
  form: FormGroup<FacilityEligibilityFormModel>,
  facilityId: string,
): UnderlyingAgreementVariationApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    draft.facilities[facilityIndex].eligibilityDetailsAndAuthorisation = {
      isConnectedToExistingFacility: form.value.isConnectedToExistingFacility,
      adjacentFacilityId: form.value.adjacentFacilityId,
      agreementType: form.value.agreementType,
      erpAuthorisationExists: form.value.erpAuthorisationExists,
      authorisationNumber: form.value.authorisationNumber,
      regulatorName: form.value.regulatorName,
      permitFile: form.value.permitFile?.uuid ?? null,
    };
  });
}

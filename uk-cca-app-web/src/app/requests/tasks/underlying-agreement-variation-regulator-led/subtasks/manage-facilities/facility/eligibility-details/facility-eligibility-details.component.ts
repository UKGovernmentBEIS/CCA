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
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { FileInputComponent, TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  EligibilityDetailsAndAuthorisation,
  UnderlyingAgreementVariationRegulatorLedSavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../../transform';
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
    this.form.controls.agreementType.valueChanges,
    {
      initialValue: this.form.controls.agreementType.value,
    },
  );

  protected readonly isEnvironmental: Signal<boolean> = computed(() => {
    const agreementType = this.agreementType();

    if (agreementType === 'ENVIRONMENTAL_PERMITTING_REGULATIONS') {
      this.form.controls.erpAuthorisationExists.enable();
      return true;
    } else {
      this.form.controls.erpAuthorisationExists.disable();
      this.form.controls.authorisationNumber.disable();
      this.form.controls.regulatorName.disable();
      this.form.controls.permitFile.disable();

      this.form.controls.erpAuthorisationExists.reset();
      this.form.controls.authorisationNumber.reset();
      this.form.controls.regulatorName.reset();
      this.form.controls.permitFile.reset();
      return false;
    }
  });

  private readonly erpAuthorisationExists: Signal<EligibilityDetailsAndAuthorisation['erpAuthorisationExists']> =
    toSignal(this.form.controls.erpAuthorisationExists.valueChanges, {
      initialValue: this.form.controls.erpAuthorisationExists.value,
    });

  protected readonly isAuthorisation: Signal<boolean> = computed(() => {
    if (this.erpAuthorisationExists()) {
      this.form.controls.authorisationNumber.enable();
      this.form.controls.regulatorName.enable();
      this.form.controls.permitFile.enable();
      return true;
    } else {
      this.form.controls.authorisationNumber.disable();
      this.form.controls.regulatorName.disable();
      this.form.controls.permitFile.disable();

      this.form.controls.authorisationNumber.reset();
      this.form.controls.regulatorName.reset();
      this.form.controls.permitFile.reset();
      return false;
    }
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);
    const updatedPayload = updateFacilityEligibilityDetails(actionPayload, this.form, this.facilityId);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)() as number;
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

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
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  form: FormGroup<FacilityEligibilityFormModel>,
  facilityId: string,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
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

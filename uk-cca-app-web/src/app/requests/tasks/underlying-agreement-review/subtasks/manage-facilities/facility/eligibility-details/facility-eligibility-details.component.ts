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
  FacilityWizardStep,
  isFacilityWizardCompleted,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { FileInputComponent, TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  EligibilityDetailsAndAuthorisation,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementReviewRequestTaskPayload,
} from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../../transform';
import { resetDetermination } from '../../../../utils';
import {
  FACILITY_ELIGIBILITY_DETAILS_FORM,
  facilityEligibilityDetailsFormProvider,
  FacilityEligibilityFormModel,
} from './facility-eligibility-details-form.provider';

@Component({
  selector: 'cca-facility-eligibility-details',
  templateUrl: './facility-eligibility-details.component.html',
  standalone: true,
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
  providers: [facilityEligibilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityEligibilityDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<FormGroup<FacilityEligibilityFormModel>>(FACILITY_ELIGIBILITY_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
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
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);

    // Create a copy of the facility with updated eligibility details
    const updatedPayload = update(actionPayload, this.facilityId, this.form);

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    // Create DTO and make API call
    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted: this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementReviewRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        if (isFacilityWizardCompleted(facility)) {
          this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
        } else {
          this.router.navigate(['../', FacilityWizardStep.EXTENT], { relativeTo: this.activatedRoute });
        }
      });
  }
}

function update(
  payload: UnderlyingAgreementApplySavePayload,
  facilityId: string,
  form: FormGroup<FacilityEligibilityFormModel>,
) {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
    if (facilityIndex === -1) return;

    // Make sure all required fields are included
    draft.facilities[facilityIndex].eligibilityDetailsAndAuthorisation = {
      isConnectedToExistingFacility: form.value.isConnectedToExistingFacility || false,
      adjacentFacilityId: form.value.adjacentFacilityId,
      agreementType: form.value.agreementType || 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
      erpAuthorisationExists: form.value.erpAuthorisationExists,
      authorisationNumber: form.value.authorisationNumber,
      regulatorName: form.value.regulatorName,
      permitFile: form.value.permitFile?.uuid ?? null,
    };
  });
}

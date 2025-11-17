import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import {
  FacilityTargetCompositionFormModel,
  FacilityWizardStep,
  isCCA3FacilityWizardCompleted,
  MeasurementTypeEnum,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  transformMeasurementType,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import {
  FileInputComponent,
  TextInputComponent as CcaTextInputComponent,
  WizardStepComponent,
} from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import {
  TargetComposition,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementReviewRequestTaskPayload,
} from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../../transform';
import { resetDetermination } from '../../../../utils';
import { TARGET_COMPOSITION_FORM, TargetCompositionFormProvider } from './target-composition-form.provider';

@Component({
  selector: 'cca-target-composition',
  templateUrl: './target-composition.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    SelectComponent,
    CcaTextInputComponent,
    RouterLink,
  ],
  providers: [TargetCompositionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetCompositionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityTargetCompositionFormModel>(TARGET_COMPOSITION_FORM);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly transformMeasurementType = transformMeasurementType;

  protected getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly measurementTypeOptions: GovukSelectOption<TargetComposition['measurementType']>[] = [
    {
      value: 'ENERGY_KWH',
      text: MeasurementTypeEnum.ENERGY_KWH,
    },
    {
      value: 'ENERGY_MWH',
      text: MeasurementTypeEnum.ENERGY_MWH,
    },
    {
      value: 'ENERGY_GJ',
      text: MeasurementTypeEnum.ENERGY_GJ,
    },
    {
      value: 'CARBON_KG',
      text: MeasurementTypeEnum.CARBON_KG,
    },
    {
      value: 'CARBON_TONNE',
      text: MeasurementTypeEnum.CARBON_TONNE,
    },
  ];

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);

    // Create a copy of the facility with updated contact details
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

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
      if (isCCA3FacilityWizardCompleted(facility)) {
        this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../', FacilityWizardStep.BASELINE_DATA], { relativeTo: this.activatedRoute });
      }
    });
  }
}

function update(
  payload: UnderlyingAgreementApplySavePayload,
  facilityId: string,
  form: FacilityTargetCompositionFormModel,
) {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
    if (facilityIndex === -1) return;

    if (draft.facilities[facilityIndex]?.cca3BaselineAndTargets?.targetComposition) {
      draft.facilities[facilityIndex].cca3BaselineAndTargets.targetComposition = {
        ...draft.facilities[facilityIndex].cca3BaselineAndTargets.targetComposition,
        ...form.value,
        calculatorFile: fileUtils.toUUIDs([form.value.calculatorFile])[0] || '',
      };
    } else {
      draft.facilities[facilityIndex].cca3BaselineAndTargets = {
        ...draft.facilities[facilityIndex].cca3BaselineAndTargets,
        targetComposition: {
          calculatorFile: fileUtils.toUUIDs([form.value.calculatorFile])[0] || '',
          measurementType: form.value.measurementType,
          agreementCompositionType: form.value.agreementCompositionType ?? 'NOVEM',
        },
      };
    }
  });
}

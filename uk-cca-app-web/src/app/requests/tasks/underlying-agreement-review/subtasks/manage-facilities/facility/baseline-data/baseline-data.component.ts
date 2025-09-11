import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  FacilityBaselineDataFormModel,
  FacilityWizardStep,
  isFacilityWizardCompleted,
  MeasurementTypeToUnitPipe,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../../transform';
import { resetDetermination } from '../../../../utils';
import { FACILITY_BASELINE_DATA_FORM, FacilityBaselineDataFormProvider } from './baseline-data-form.provider';

@Component({
  selector: 'cca-baseline-data',
  templateUrl: './baseline-data.component.html',
  standalone: true,
  imports: [
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    DateInputComponent,
    TextareaComponent,
    TextInputComponent,
    MultipleFileInputComponent,
    MeasurementTypeToUnitPipe,
    RouterLink,
    WizardStepComponent,
  ],
  providers: [FacilityBaselineDataFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineDataComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityBaselineDataFormModel>(FACILITY_BASELINE_DATA_FORM);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly una = this.store.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
  private readonly facilityIndex = this.una.facilities?.findIndex((f) => f.facilityId === this.facilityId) ?? -1;

  protected readonly downloadUrl = generateDownloadUrl(this.taskId.toString());

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly isTwelveMonthsValue = toSignal(this.form.controls.isTwelveMonths.valueChanges, {
    initialValue: this.form.controls.isTwelveMonths.value,
  });

  protected readonly twelveMonthsSelected = computed(() => typeof this.isTwelveMonthsValue() === 'boolean');

  protected readonly baselineDateValue = toSignal(this.form.controls.baselineDate.valueChanges, {
    initialValue: this.form.value.baselineDate,
  });

  protected readonly dateIsGreaterThanStartOf2022 = computed(
    () => this.baselineDateValue() && this.baselineDateValue().getTime() > new Date('2022-01-01').getTime(),
  );

  protected readonly targetComposition = this.store.select(
    underlyingAgreementQuery.selectFacilityTargetComposition(this.facilityIndex),
  )();

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

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementReviewRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        if (isFacilityWizardCompleted(facility)) {
          this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
        } else {
          this.router.navigate(['../', FacilityWizardStep.TARGETS], { relativeTo: this.activatedRoute });
        }
      });
  }
}

function update(payload: UnderlyingAgreementApplySavePayload, facilityId: string, form: FacilityBaselineDataFormModel) {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
    if (facilityIndex === -1) return;

    const baselineDate = form.controls.baselineDate?.value;
    const greenfieldEvidences = fileUtils.toUUIDs(form.controls.greenfieldEvidences?.value);

    draft.facilities[facilityIndex].cca3BaselineAndTargets.baselineData = {
      isTwelveMonths: form.controls.isTwelveMonths?.value,
      baselineDate: baselineDate ? baselineDate.toISOString().split('T')[0] : null, // Format as YYYY-MM-DD
      explanation: form.controls.explanation?.value,
      greenfieldEvidences: Array.isArray(greenfieldEvidences) ? greenfieldEvidences : [],
      energy: form.controls.energy?.value,
      usedReportingMechanism: form.controls.usedReportingMechanism?.value,
      energyCarbonFactor: form.controls.energyCarbonFactor?.value,
    };
  });
}

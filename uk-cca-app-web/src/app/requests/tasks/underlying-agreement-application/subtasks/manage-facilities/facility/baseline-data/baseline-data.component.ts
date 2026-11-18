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
  FACILITY_BASELINE_DATA_FORM,
  FacilityBaselineDataFormModel,
  FacilityBaselineDataFormProvider,
  FacilityWizardStep,
  isCCA3FacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../transform';

@Component({
  selector: 'cca-baseline-data',
  templateUrl: './baseline-data.component.html',
  imports: [
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    DateInputComponent,
    TextareaComponent,
    TextInputComponent,
    MultipleFileInputComponent,
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
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = update(actionPayload, this.form, this.facilityId, this.dateIsGreaterThanStartOf2022());

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementSubmitRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        if (isCCA3FacilityWizardCompleted(facility)) {
          this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
        } else {
          this.router.navigate(['../', FacilityWizardStep.BASELINE_ENERGY_CONSUMPTION], {
            relativeTo: this.activatedRoute,
          });
        }
      });
  }
}

function update(
  payload: UnderlyingAgreementApplySavePayload,
  form: FacilityBaselineDataFormModel,
  facilityId: string,
  dateIsGreaterThanStartOf2022: boolean,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const baselineDate = form.controls.baselineDate?.value;
    const greenfieldEvidences = fileUtils.toUUIDs(form.controls.greenfieldEvidences?.value);
    const isTwelveMonths = form.controls.isTwelveMonths?.value;

    draft.facilities[facilityIndex].cca3BaselineAndTargets.baselineData = {
      isTwelveMonths,
      baselineDate: baselineDate ? baselineDate.toISOString().split('T')[0] : null, // Format as YYYY-MM-DD
      explanation: !isTwelveMonths || dateIsGreaterThanStartOf2022 ? form.controls.explanation?.value : null,
      greenfieldEvidences: Array.isArray(greenfieldEvidences) && !isTwelveMonths ? greenfieldEvidences : [],
      energy: form.controls.energy?.value,
      usedReportingMechanism: form.controls.usedReportingMechanism?.value,
      energyCarbonFactor: form.controls.energyCarbonFactor?.value,
    };
  });
}

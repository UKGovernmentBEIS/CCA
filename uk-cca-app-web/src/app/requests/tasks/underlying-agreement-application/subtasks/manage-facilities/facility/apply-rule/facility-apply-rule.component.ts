import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, TextInputComponent } from '@netz/govuk-components';
import {
  calculateEnergyConsumedEligible,
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';
import { produce } from 'immer';

import { Apply70Rule, UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../transform';
import {
  FACILITY_APPLY_RULE_FORM,
  FacilityApplyRuleFormModel,
  FacilityApplyRuleFormProvider,
} from './facility-apply-rule-form.provider';

@Component({
  selector: 'cca-facility-apply-rule',
  templateUrl: './facility-apply-rule.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    FileInputComponent,
    TextInputComponent,
    DateInputComponent,
    DecimalPipe,
    RouterLink,
  ],
  providers: [FacilityApplyRuleFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityApplyRuleComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<FormGroup<FacilityApplyRuleFormModel>>(FACILITY_APPLY_RULE_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  private readonly energyConsumedValue: Signal<Apply70Rule['energyConsumed']> = toSignal(
    this.form.controls.energyConsumed.valueChanges,
    {
      initialValue: this.form.controls.energyConsumed.value,
    },
  );

  private readonly energyConsumedProvisionValue: Signal<Apply70Rule['energyConsumedProvision']> = toSignal(
    this.form.controls.energyConsumedProvision.valueChanges,
    {
      initialValue: this.form.controls.energyConsumedProvision.value,
    },
  );

  protected readonly isLessThan70 = computed(
    () =>
      this.energyConsumedValue() !== null &&
      this.energyConsumedValue() !== '' &&
      Number(this.energyConsumedValue()) < 70,
  );

  protected readonly energyConsumedEligible = computed(() => {
    const energyConsumed = Number(this.energyConsumedValue());
    const energyConsumedProvision = Number(this.energyConsumedProvisionValue());

    if (energyConsumed >= 70) return 100;
    if (energyConsumed === 0) return 0;

    if (energyConsumedProvision >= 0 && energyConsumed > 0) {
      return calculateEnergyConsumedEligible(energyConsumed, energyConsumedProvision);
    }

    return null;
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateFacilityApplyRule(
      actionPayload,
      this.form,
      this.facilityId,
      this.energyConsumedEligible(),
    );

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
        if (isFacilityWizardCompleted(facility)) {
          this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
        } else {
          this.router.navigate(['../', FacilityWizardStep.TARGET_COMPOSITION], { relativeTo: this.activatedRoute });
        }
      });
  }
}

function updateFacilityApplyRule(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<FacilityApplyRuleFormModel>,
  facilityId: string,
  energyConsumedEligible: number | null,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    if (!draft.facilities[facilityIndex].apply70Rule) {
      draft.facilities[facilityIndex].apply70Rule = {
        energyConsumed: form.value.energyConsumed,
        energyConsumedProvision: form.value.energyConsumedProvision,
        startDate: form.value.startDate,
        energyConsumedEligible: energyConsumedEligible !== null ? String(energyConsumedEligible) : null,
        evidenceFile: fileUtils.toUUIDs([form.value.evidenceFile])[0] || '',
      };
    } else {
      draft.facilities[facilityIndex].apply70Rule = {
        ...draft.facilities[facilityIndex].apply70Rule,
        ...form.value,
        energyConsumedEligible: energyConsumedEligible !== null ? String(energyConsumedEligible) : null,
        evidenceFile: fileUtils.toUUIDs([form.value.evidenceFile])[0] || '',
      };
    }
  });
}

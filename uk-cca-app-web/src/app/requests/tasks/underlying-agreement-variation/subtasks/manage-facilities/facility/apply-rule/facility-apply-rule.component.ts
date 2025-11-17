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
import { produce } from 'immer';

import {
  Apply70Rule,
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../transform';
import { extractReviewProps } from '../../../../utils';
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
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<FormGroup<FacilityApplyRuleFormModel>>(FACILITY_APPLY_RULE_FORM);

  private readonly facilityId = this.route.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
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
    () => this.energyConsumedValue() && Number(this.energyConsumedValue()) < 70,
  );

  protected readonly energyConsumedEligible: Signal<number | null> = computed(() => {
    const energyConsumed = this.energyConsumedValue();
    const energyConsumedProvision = this.energyConsumedProvisionValue();

    if (Number(energyConsumed) >= 70) return 100;
    if (Number(energyConsumed) == 0) return 0;

    if (energyConsumedProvision && Number(energyConsumed) > 0)
      return calculateEnergyConsumedEligible(energyConsumed, energyConsumedProvision);
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const updatedPayload = updateFacilityApplyRule(
      actionPayload,
      this.form,
      this.facilityId,
      this.energyConsumedEligible(),
    );

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
          this.router.navigate(['../', FacilityWizardStep.TARGET_COMPOSITION], { relativeTo: this.route });
        }
      });
  }
}

function updateFacilityApplyRule(
  payload: UnderlyingAgreementVariationApplySavePayload,
  form: FormGroup<FacilityApplyRuleFormModel>,
  facilityId: string,
  energyConsumedEligible: number | null,
): UnderlyingAgreementVariationApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    draft.facilities[facilityIndex].apply70Rule = {
      energyConsumed: form.value.energyConsumed,
      energyConsumedProvision: form.value.energyConsumedProvision,
      startDate: form.value.startDate,
      energyConsumedEligible: String(energyConsumedEligible),
      evidenceFile: form.value.evidenceFile?.uuid ?? null,
    };
  });
}

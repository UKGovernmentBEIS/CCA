import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, TextInputComponent } from '@netz/govuk-components';
import {
  calculateEnergyConsumedEligible,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { Apply70Rule } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import {
  FACILITY_APPLY_RULE_FORM,
  FacilityApplyRuleFormModel,
  facilityApplyRuleFormProvider,
} from './facility-apply-rule-form.provider';

@Component({
  selector: 'cca-facility-apply-rule',
  templateUrl: './facility-apply-rule.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    FileInputComponent,
    TextInputComponent,
    DateInputComponent,
    DecimalPipe,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [facilityApplyRuleFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityApplyRuleComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<FormGroup<FacilityApplyRuleFormModel>>(FACILITY_APPLY_RULE_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))();

  private readonly energyConsumedValue: Signal<Apply70Rule['energyConsumed']> = toSignal(
    this.form.get('energyConsumed').valueChanges,
    {
      initialValue: this.form.get('energyConsumed').value,
    },
  );

  private readonly energyConsumedProvisionValue: Signal<Apply70Rule['energyConsumedProvision']> = toSignal(
    this.form.get('energyConsumedProvision').valueChanges,
    {
      initialValue: this.form.get('energyConsumedProvision').value,
    },
  );

  protected readonly isLessThan70 = computed(() => {
    return this.energyConsumedValue() && this.energyConsumedValue() < 70;
  });

  protected readonly energyConsumedEligible: Signal<number | null> = computed(() => {
    const energyConsumed = this.energyConsumedValue();
    const energyConsumedProvision = this.energyConsumedProvisionValue();

    if (energyConsumed >= 70) return 100;
    if (energyConsumed == 0) return 0;

    if (energyConsumedProvision && energyConsumed > 0)
      return calculateEnergyConsumedEligible(energyConsumed, energyConsumedProvision);

    return null;
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;
    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    // Create a copy of the facility with updated apply rule data
    const updatedPayload = produce(actionPayload, (draft) => {
      const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === this.facilityId);

      if (facilityIndex >= 0) {
        // Make sure all required properties have values
        draft.facilities[facilityIndex].apply70Rule = {
          energyConsumed: this.form.value.energyConsumed,
          energyConsumedProvision: this.form.value.energyConsumedProvision,
          energyConsumedEligible: this.energyConsumedEligible(),
          startDate: this.form.value.startDate,
          evidenceFile: this.form.value.evidenceFile?.uuid ?? null,
        };
      }
    });

    // Update section statuses
    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

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
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}

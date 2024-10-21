import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, DetailsComponent, TextInputComponent } from '@netz/govuk-components';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { TextInputComponent as CcaTextInputComponent } from '@shared/components/text-input/text-input.component';
import { transformFilesToAttachments, transformFilesToUUIDsList } from '@shared/utils';

import { Apply70Rule } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import { calculateEnergyConsumedEligible } from '../facility.helper';
import {
  FACILITY_APPLY_RULE_FORM,
  FacilityApplyRuleFormModel,
  FacilityApplyRuleFormProvider,
} from './facility-apply-rule-form.provider';

@Component({
  selector: 'cca-facility-apply-rule',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    FileInputComponent,
    DetailsComponent,
    TextInputComponent,
    DateInputComponent,
    CcaTextInputComponent,
    DecimalPipe,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './facility-apply-rule.component.html',
  providers: [FacilityApplyRuleFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityApplyRuleComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityApplyRuleFormModel>>(FACILITY_APPLY_RULE_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

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
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(underlyingAgreementQuery.selectPayload)();
    this.requestTaskStore.setPayload({ ...payload, currentFacilityId: this.facilityId });

    this.taskService
      .saveSubtask(FACILITIES_SUBTASK, FacilityWizardStep.APPLY_RULE, this.activatedRoute, {
        facility: {
          facilityId: this.facilityId,
          apply70Rule: {
            ...this.form.value,
            energyConsumedEligible: this.energyConsumedEligible(),
            evidenceFile: transformFilesToUUIDsList(this.form.value.evidenceFile),
          },
        },
        attachments: transformFilesToAttachments([this.form.value.evidenceFile]),
      })
      .subscribe();
  }
}

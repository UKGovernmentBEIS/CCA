import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus, TasksApiService, underlyingAgreementQuery } from '@requests/common';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { Improvement } from '@shared/types';
import { produce } from 'immer';

import {
  FacilityTargets,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../transform';
import { FACILITY_TARGETS_FORM, FacilityTargetsFormModel, FacilityTargetsFormProvider } from './targets-form.provider';

@Component({
  selector: 'cca-targets',
  templateUrl: './targets.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, WizardStepComponent, TextInputComponent, RouterLink],
  providers: [FacilityTargetsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityTargetsFormModel>(FACILITY_TARGETS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateFacilityTargets(actionPayload, this.form, this.facilityId);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function updateFacilityTargets(
  payload: UnderlyingAgreementApplySavePayload,
  form: FacilityTargetsFormModel,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const improvements: FacilityTargets['improvements'] = {
      [Improvement.TP7]: form.value.tp7,
      [Improvement.TP8]: form.value.tp8,
      [Improvement.TP9]: form.value.tp9,
    };

    draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityTargets = {
      improvements,
    };
  });
}

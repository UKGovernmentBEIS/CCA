import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  FacilityExtent,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementReviewRequestTaskPayload,
} from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../../transform';
import { resetDetermination } from '../../../../utils';
import {
  FACILITY_EXTENT_FORM,
  FacilityExtentFormModel,
  facilityExtentFormProvider,
} from './facility-extent-form.provider';

@Component({
  selector: 'cca-facility-extent',
  templateUrl: './facility-extent.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    FileInputComponent,
    DetailsComponent,
    RouterLink,
  ],
  providers: [facilityExtentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityExtentComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<FormGroup<FacilityExtentFormModel>>(FACILITY_EXTENT_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  private readonly activitiesClaimedExists: Signal<FacilityExtent['areActivitiesClaimed']> = toSignal(
    this.form.get('areActivitiesClaimed').valueChanges,
    {
      initialValue: this.form.get('areActivitiesClaimed').value,
    },
  );

  protected readonly isActivitiesDescriptionFileExist: Signal<boolean> = computed(() => {
    if (this.activitiesClaimedExists()) {
      this.form.get('activitiesDescriptionFile').enable();
      return true;
    } else {
      this.form.get('activitiesDescriptionFile').disable();
      this.form.get('activitiesDescriptionFile').reset();
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

    // Create a copy of the facility with updated extent details
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
          this.router.navigate(['../', FacilityWizardStep.APPLY_RULE], { relativeTo: this.activatedRoute });
        }
      });
  }
}

function update(
  payload: UnderlyingAgreementApplySavePayload,
  facilityId: string,
  form: FormGroup<FacilityExtentFormModel>,
) {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === facilityId);
    if (facilityIndex === -1) return;

    // Make sure all required fields are included
    draft.facilities[facilityIndex].facilityExtent = {
      areActivitiesClaimed: form.value.areActivitiesClaimed || false,
      manufacturingProcessFile: form.value.manufacturingProcessFile?.uuid ?? null,
      processFlowFile: form.value.processFlowFile?.uuid ?? null,
      annotatedSitePlansFile: form.value.annotatedSitePlansFile?.uuid ?? null,
      eligibleProcessFile: form.value.eligibleProcessFile?.uuid ?? null,
      activitiesDescriptionFile: form.value?.activitiesDescriptionFile?.uuid ?? null,
    };
  });
}

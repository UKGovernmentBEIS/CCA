import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { FacilityExtent } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import {
  FACILITY_EXTENT_FORM,
  FacilityExtentFormModel,
  facilityExtentFormProvider,
} from './facility-extent-form.provider';

@Component({
  selector: 'cca-facility-extent',
  templateUrl: './facility-extent.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    FileInputComponent,
    DetailsComponent,
    ReturnToTaskOrActionPageComponent,
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

  protected readonly facility = this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))();

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
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    // Create a copy of the facility with updated extent details
    const updatedPayload = produce(actionPayload, (draft) => {
      const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === this.facilityId);

      if (facilityIndex >= 0) {
        // Make sure all required fields are included
        draft.facilities[facilityIndex].facilityExtent = {
          areActivitiesClaimed: this.form.value.areActivitiesClaimed,
          manufacturingProcessFile: this.form.value.manufacturingProcessFile?.uuid ?? null,
          processFlowFile: this.form.value.processFlowFile?.uuid ?? null,
          annotatedSitePlansFile: this.form.value.annotatedSitePlansFile?.uuid ?? null,
          eligibleProcessFile: this.form.value.eligibleProcessFile?.uuid ?? null,
          activitiesDescriptionFile: this.form.value?.activitiesDescriptionFile?.uuid ?? null,
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

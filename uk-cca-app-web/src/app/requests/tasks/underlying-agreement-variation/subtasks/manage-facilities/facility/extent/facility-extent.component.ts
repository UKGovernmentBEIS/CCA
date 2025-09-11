import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { FacilityExtent, UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../transform';
import { extractReviewProps } from '../../../../utils';
import {
  FACILITY_EXTENT_FORM,
  FacilityExtentFormModel,
  FacilityExtentFormProvider,
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
    RouterLink,
  ],
  providers: [FacilityExtentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityExtentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<FormGroup<FacilityExtentFormModel>>(FACILITY_EXTENT_FORM);

  private readonly facilityId = this.route.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
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
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Update facility extent
    const updatedPayload = produce(actionPayload, (draft) => {
      const facility = draft.facilities.find((f) => f.facilityId === this.facilityId);
      if (facility) {
        facility.facilityExtent = {
          areActivitiesClaimed: this.form.value.areActivitiesClaimed,
          manufacturingProcessFile: this.form.value.manufacturingProcessFile?.uuid ?? null,
          processFlowFile: this.form.value.processFlowFile?.uuid ?? null,
          annotatedSitePlansFile: this.form.value.annotatedSitePlansFile?.uuid ?? null,
          eligibleProcessFile: this.form.value.eligibleProcessFile?.uuid ?? null,
          activitiesDescriptionFile: this.form.value?.activitiesDescriptionFile?.uuid ?? null,
        };
      }
    });

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
          this.router.navigate([`../${FacilityWizardStep.APPLY_RULE}`], { relativeTo: this.route });
        }
      });
  }
}

import { ChangeDetectionStrategy, Component, computed, effect, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import {
  FACILITY_EXTENT_FORM,
  FacilityExtentFormModel,
  FacilityExtentFormProvider,
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import {
  FacilityExtent,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../transform';

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
  providers: [FacilityExtentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityExtentComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

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

  protected readonly isActivitiesDescriptionFileExist: Signal<boolean> = computed(() =>
    Boolean(this.activitiesClaimedExists()),
  );

  constructor() {
    effect(() => {
      const activitiesDescriptionFile = this.form.get('activitiesDescriptionFile');

      if (this.isActivitiesDescriptionFileExist()) {
        activitiesDescriptionFile.enable();
      } else {
        activitiesDescriptionFile.disable();
        activitiesDescriptionFile.reset();
      }
    });
  }

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateFacilityExtent(actionPayload, this.form, this.facilityId);

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
          this.router.navigate(['../', FacilityWizardStep.APPLY_RULE], { relativeTo: this.activatedRoute });
        }
      });
  }
}

function updateFacilityExtent(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<FacilityExtentFormModel>,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    // `form.value` leaves disabled controls out, so a control that the effect disabled and reset
    // would keep its previously saved value. Read the raw value so the reset is saved as well.
    const value = form.getRawValue();

    if (!draft.facilities[facilityIndex].facilityExtent) {
      draft.facilities[facilityIndex].facilityExtent = {
        areActivitiesClaimed: value.areActivitiesClaimed,
        manufacturingProcessFile: value.manufacturingProcessFile?.uuid ?? '',
        processFlowFile: value.processFlowFile?.uuid ?? '',
        annotatedSitePlansFile: value.annotatedSitePlansFile?.uuid ?? '',
        eligibleProcessFile: value.eligibleProcessFile?.uuid ?? '',
        activitiesDescriptionFile: value.activitiesDescriptionFile?.uuid ?? '',
      };
    } else {
      draft.facilities[facilityIndex].facilityExtent = {
        ...draft.facilities[facilityIndex].facilityExtent,
        ...value,
        manufacturingProcessFile: value.manufacturingProcessFile?.uuid ?? '',
        processFlowFile: value.processFlowFile?.uuid ?? '',
        annotatedSitePlansFile: value.annotatedSitePlansFile?.uuid ?? '',
        eligibleProcessFile: value.eligibleProcessFile?.uuid ?? '',
        activitiesDescriptionFile: value.activitiesDescriptionFile?.uuid ?? '',
      };
    }
  });
}

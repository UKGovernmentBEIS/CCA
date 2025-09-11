import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CheckboxComponent,
  CheckboxesComponent,
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
} from '@netz/govuk-components';
import {
  ApplicationReasonTypePipe,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
} from '@requests/common';
import { AccountAddressInputComponent, TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import {
  FACILITY_DETAILS_FORM,
  FacilityDetailsFormModel,
  facilityDetailsFormProvider,
} from './facility-details-form.provider';

@Component({
  selector: 'cca-facility-details',
  templateUrl: './facility-details.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    ApplicationReasonTypePipe,
    CheckboxComponent,
    CheckboxesComponent,
    AccountAddressInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [facilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<FormGroup<FacilityDetailsFormModel>>(FACILITY_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))();

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    // Create a copy of the facility with updated details
    const updatedPayload = produce(actionPayload, (draft) => {
      const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === this.facilityId);

      if (facilityIndex >= 0) {
        draft.facilities[facilityIndex].facilityDetails = {
          ...draft.facilities[facilityIndex].facilityDetails,
          name: this.form.controls.name.value,
          isCoveredByUkets: this.form.value.isCoveredByUkets,
          uketsId: this.form.value.uketsId,
          applicationReason: this.form.getRawValue().applicationReason,
          previousFacilityId: this.form.getRawValue().previousFacilityId,
          facilityAddress: this.form.getRawValue().facilityAddress,
        };
      }
    });

    // Update section statuses
    const reviewSectionsCompleted = produce(payload.reviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const determination = resetDetermination(payload.determination);

    // Create DTO and make API call
    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted: this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}

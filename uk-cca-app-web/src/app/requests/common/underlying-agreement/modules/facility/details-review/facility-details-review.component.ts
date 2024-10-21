import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import {
  CheckboxComponent,
  CheckboxesComponent,
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
} from '@netz/govuk-components';
import {
  ApplicationReasonTypePipe,
  FACILITIES_SUBTASK,
  FacilityWizardStep,
  underlyingAgreementQuery,
} from '@requests/common';
import { AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { TextInputComponent } from '@shared/components/text-input/text-input.component';

import {
  FACILITY_DETAILS_REVIEW_FORM,
  FacilityDetailsReviewFormModel,
  FacilityDetailsReviewFormProvider,
} from './facility-details-review-form.provider';

@Component({
  selector: 'cca-facility-details-review',
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
  templateUrl: './facility-details-review.component.html',
  providers: [FacilityDetailsReviewFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsReviewComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityDetailsReviewFormModel>>(FACILITY_DETAILS_REVIEW_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacility(this.facilityId),
  )();

  onSubmit() {
    const payload = this.requestTaskStore.select(underlyingAgreementQuery.selectPayload)();
    this.requestTaskStore.setPayload({ ...payload, currentFacilityId: this.facilityId });

    this.taskService
      .saveSubtask(FACILITIES_SUBTASK, FacilityWizardStep.DETAILS, this.activatedRoute, {
        facility: {
          facilityId: this.facilityId,

          facilityDetails: {
            name: this.form.controls.name.value,
            isCoveredByUkets: this.form.value.isCoveredByUkets,
            uketsId: this.form.value.uketsId,
            applicationReason: this.form.value.applicationReason,
            previousFacilityId: this.form.value.previousFacilityId,
            facilityAddress: this.form.getRawValue().facilityAddress,
          },
        },
      })
      .subscribe();
  }
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
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
import { AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { TextInputComponent } from '@shared/components/text-input/text-input.component';

import { underlyingAgreementQuery } from '../../../+state';
import { ApplicationReasonTypePipe } from '../../../pipes';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import {
  FACILITY_DETAILS_FORM,
  FacilityDetailsFormModel,
  FacilityDetailsFormProvider,
} from './facility-details-form.provider';

@Component({
  selector: 'cca-facility-details',
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
  templateUrl: './facility-details.component.html',
  providers: [FacilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityDetailsFormModel>>(FACILITY_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

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

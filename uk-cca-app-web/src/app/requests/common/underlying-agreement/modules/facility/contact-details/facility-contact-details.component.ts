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
  TextInputComponent,
} from '@netz/govuk-components';
import { AccountAddressInputComponent, PhoneInputComponent, WizardStepComponent } from '@shared/components';

import { underlyingAgreementQuery } from '../../../+state';
import { ApplicationReasonTypePipe } from '../../../pipes';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import {
  FACILITY_CONTACT_DETAILS_FORM,
  FacilityContactDetailsFormProvider,
  FacilityContactFormModel,
} from './facility-contact-details-form.provider';

@Component({
  selector: 'cca-facility-contact-details',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    ApplicationReasonTypePipe,
    CheckboxComponent,
    CheckboxesComponent,
    AccountAddressInputComponent,
    TextInputComponent,
    PhoneInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './facility-contact-details.component.html',
  providers: [FacilityContactDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityContactDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityContactFormModel>>(FACILITY_CONTACT_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  onSubmit() {
    const facility = {
      facilityId: this.facilityId,
      facilityContact: {
        firstName: this.form.getRawValue().firstName,
        lastName: this.form.getRawValue().lastName,
        email: this.form.getRawValue().email,
        address: this.form.getRawValue().address,
        phoneNumber: this.form.getRawValue().phoneNumber,
      },
    };
    this.taskService
      .saveSubtask(FACILITIES_SUBTASK, FacilityWizardStep.CONTACT_DETAILS, this.activatedRoute, facility)
      .subscribe();
  }
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, TextInputComponent } from '@netz/govuk-components';
import {
  FacilityWizardStep,
  isFacilityWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { AccountAddressInputComponent, PhoneInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../transform';
import {
  FACILITY_CONTACT_DETAILS_FORM,
  FacilityContactDetailsFormProvider,
  FacilityContactFormModel,
} from './facility-contact-details-form.provider';

@Component({
  selector: 'cca-facility-contact-details',
  templateUrl: './facility-contact-details.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    CheckboxComponent,
    CheckboxesComponent,
    AccountAddressInputComponent,
    TextInputComponent,
    PhoneInputComponent,
    RouterLink,
  ],
  providers: [FacilityContactDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityContactDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<FormGroup<FacilityContactFormModel>>(FACILITY_CONTACT_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateFacilityContact(actionPayload, this.form, this.facilityId);

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
          this.router.navigate(['../', FacilityWizardStep.ELIGIBILITY_DETAILS], { relativeTo: this.activatedRoute });
        }
      });
  }
}

function updateFacilityContact(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<FacilityContactFormModel>,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    draft.facilities[facilityIndex].facilityContact = {
      firstName: form.getRawValue().firstName,
      lastName: form.getRawValue().lastName,
      email: form.getRawValue().email,
      address: form.getRawValue().address,
      phoneNumber: form.getRawValue().phoneNumber,
    };
  });
}

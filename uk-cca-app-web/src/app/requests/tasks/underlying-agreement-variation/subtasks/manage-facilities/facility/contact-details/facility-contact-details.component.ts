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

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../transform';
import { extractReviewProps } from '../../../../utils';
import {
  FACILITY_CONTACT_DETAILS_FORM,
  FacilityContactDetailsFormProvider,
  FacilityContactFormModel,
} from './facility-contact-details-form.provider';

@Component({
  selector: 'cca-facility-contact-details',
  templateUrl: './facility-contact-details.component.html',
  standalone: true,
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
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<FormGroup<FacilityContactFormModel>>(FACILITY_CONTACT_DETAILS_FORM);

  private readonly facilityId = this.route.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Update facility contact details
    const updatedPayload = produce(actionPayload, (draft) => {
      const facility = draft.facilities.find((f) => f.facilityId === this.facilityId);
      if (facility) {
        facility.facilityContact = {
          firstName: this.form.getRawValue().firstName,
          lastName: this.form.getRawValue().lastName,
          email: this.form.getRawValue().email,
          address: this.form.getRawValue().address,
          phoneNumber: this.form.getRawValue().phoneNumber,
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
          this.router.navigate([`../${FacilityWizardStep.ELIGIBILITY_DETAILS}`], { relativeTo: this.route });
        }
      });
  }
}

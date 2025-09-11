import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, TextInputComponent } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { AccountAddressInputComponent, PhoneInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import {
  FACILITY_CONTACT_DETAILS_FORM,
  facilityContactDetailsFormProvider,
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
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [facilityContactDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityContactDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<FormGroup<FacilityContactFormModel>>(FACILITY_CONTACT_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))();

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    // Create a copy of the facility with updated contact details
    const updatedPayload = produce(actionPayload, (draft) => {
      const facilityIndex = draft.facilities.findIndex((f) => f.facilityId === this.facilityId);

      if (facilityIndex >= 0) {
        draft.facilities[facilityIndex].facilityContact = {
          firstName: this.form.getRawValue().firstName,
          lastName: this.form.getRawValue().lastName,
          email: this.form.getRawValue().email,
          address: this.form.getRawValue().address,
          phoneNumber: this.form.getRawValue().phoneNumber,
        };
      }
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
      sectionsCompleted: this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}

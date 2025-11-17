import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, TextInputComponent } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  FacilityWizardStep,
  filterFieldsWithFalsyValues,
  isFacilityWizardCompleted,
  OVERALL_DECISION_SUBTASK,
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { AccountAddressInputComponent, PhoneInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { deleteFacilityDecision, resetDetermination } from '../../../../utils';
import {
  FACILITY_CONTACT_DETAILS_FORM,
  facilityContactDetailsFormProvider,
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

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = updateFacilityContact(actionPayload, this.form, this.facilityId);

    const originalFacility = originalPayload?.underlyingAgreement?.facilities?.find(
      (f) => f.facilityId === this.facilityId,
    );
    const currentFacility = updatedPayload.facilities?.find((f) => f.facilityId === this.facilityId);

    let areIdentical = false;

    if (originalFacility) {
      const resetOriginal = resetFacilityNonComparisonFields(originalFacility);
      const resetCurrent = resetFacilityNonComparisonFields(currentFacility);

      const filterOriginal = filterFieldsWithFalsyValues(resetOriginal);
      const filterCurrent = filterFieldsWithFalsyValues(resetCurrent);

      areIdentical = areEntitiesIdentical(filterCurrent, filterOriginal);
    }

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions)();
    const decisions = areIdentical ? deleteFacilityDecision(currentDecisions, this.facilityId) : currentDecisions;

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

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)(),
      facilitiesReviewGroupDecisions: decisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationReviewRequestTaskPayload) => {
      const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
      if (isFacilityWizardCompleted(facility)) {
        const targetPath = areIdentical ? '../check-your-answers' : '../decision';
        this.router.navigate([targetPath], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../', FacilityWizardStep.ELIGIBILITY_DETAILS], { relativeTo: this.activatedRoute });
      }
    });
  }
}

function updateFacilityContact(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FormGroup<FacilityContactFormModel>,
  facilityId: string,
): UnderlyingAgreementVariationReviewSavePayload {
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

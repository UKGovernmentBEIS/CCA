import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  areEntitiesIdentical,
  filterFieldsWithFalsyValues,
  RESPONSIBLE_PERSON_FORM,
  ResponsiblePersonFormProvider,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TasksApiService,
  transformAccountReferenceData,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { ResponsiblePersonFormModel, ResponsiblePersonInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { applySaveActionSideEffects, deleteDecision } from '../../../utils';

@Component({
  selector: 'cca-responsible-person',
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Change"
      heading="Responsible person"
      data-testid="change-responsible-person-form"
    >
      <cca-responsible-person-input />
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    WizardStepComponent,
    ResponsiblePersonInputComponent,
    ReturnToTaskOrActionPageComponent,
    ReactiveFormsModule,
  ],
  providers: [ResponsiblePersonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponsiblePersonComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<ResponsiblePersonFormModel>>(RESPONSIBLE_PERSON_FORM);

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalAccountReferenceData = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.accountReferenceData;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const updatedPayload = update(actionPayload, this.form);

    const originalTUDetails = transformAccountReferenceData(originalAccountReferenceData);
    const currentTUDetails = updatedPayload.underlyingAgreementTargetUnitDetails;

    const areIdentical = areEntitiesIdentical(
      filterFieldsWithFalsyValues(currentTUDetails),
      filterFieldsWithFalsyValues(originalTUDetails),
    );

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)();
    const decisions = areIdentical ? currentDecisions : deleteDecision(currentDecisions, 'TARGET_UNIT_DETAILS');

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: decisions,
      facilitiesReviewGroupDecisions: this.store.select(
        underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
      )(),
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const path = areIdentical ? '../check-your-answers' : '../decision';
      this.router.navigate([path], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FormGroup<ResponsiblePersonFormModel>,
): UnderlyingAgreementVariationReviewSavePayload {
  const formValue = form.getRawValue();

  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.responsiblePersonDetails = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      address: formValue.sameAddress?.[0]
        ? draft.underlyingAgreementTargetUnitDetails.operatorAddress // Use operator address if same
        : {
            line1: formValue.address.line1,
            line2: formValue.address.line2,
            city: formValue.address.city,
            county: formValue.address.county,
            postcode: formValue.address.postcode,
            country: formValue.address.country,
          },
    };
  });
}

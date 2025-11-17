import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  areEntitiesIdentical,
  filterFieldsWithFalsyValues,
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TasksApiService,
  transformAccountReferenceData,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { AccountAddressFormModel, AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { applySaveActionSideEffects, deleteDecision } from '../../../utils';
import { OPERATOR_ADDRESS_FORM, OperatorAddressFormProvider } from './operator-address-form.provider';

@Component({
  selector: 'cca-operator-address',
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Change"
      heading="Operator address"
      data-testid="change-operator-address-form"
    >
      <cca-account-address-input />
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [WizardStepComponent, AccountAddressInputComponent, ReturnToTaskOrActionPageComponent, ReactiveFormsModule],
  providers: [OperatorAddressFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAddressComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<AccountAddressFormModel>>(OPERATOR_ADDRESS_FORM);

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalAccountReferenceData = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.accountReferenceData;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.underlyingAgreementTargetUnitDetails.operatorAddress = this.form.getRawValue();
    });

    const originalTUDetails = transformAccountReferenceData(originalAccountReferenceData);
    const currentTUDetails = updatedPayload.underlyingAgreementTargetUnitDetails;

    const areIdentical = areEntitiesIdentical(
      filterFieldsWithFalsyValues(currentTUDetails),
      filterFieldsWithFalsyValues(originalTUDetails),
    );

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)();
    const decisions = areIdentical ? deleteDecision(currentDecisions, 'TARGET_UNIT_DETAILS') : currentDecisions;

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

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationReviewRequestTaskPayload) => {
        let path = '';

        if (areIdentical) {
          path = '../check-your-answers';
        } else {
          path = isTargetUnitDetailsWizardCompleted(payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails)
            ? '../decision'
            : `../${ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON}`;
        }

        this.router.navigate([path], { relativeTo: this.activatedRoute });
      });
  }
}

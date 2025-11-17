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
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { AccountAddressFormModel, AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../transform';
import { applySaveActionSideEffects } from '../../../utils';
import { UNA_OPERATOR_ADDRESS_FORM, UnaOperatorAddressFormProvider } from './una-operator-address-form.provider';

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
      <cca-account-address-input data-testid="account-address-input" />
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [ReactiveFormsModule, WizardStepComponent, AccountAddressInputComponent, ReturnToTaskOrActionPageComponent],
  providers: [UnaOperatorAddressFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAddressComponent {
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FormGroup<AccountAddressFormModel>>(UNA_OPERATOR_ADDRESS_FORM);

  onSubmit() {
    const payload = this.store.select(requestTaskQuery.selectRequestTaskPayload)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);
    const updatedPayload = updateOperatorAddress(actionPayload, this.form);

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      determination,
      reviewSectionsCompleted,
      sectionsCompleted,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementReviewRequestTaskPayload) => {
        const tuDetails = payload.underlyingAgreement.underlyingAgreementTargetUnitDetails;
        const completed = isTargetUnitDetailsWizardCompleted(tuDetails);

        const isSameAddress = areEntitiesIdentical(
          filterFieldsWithFalsyValues(tuDetails.operatorAddress),
          filterFieldsWithFalsyValues(tuDetails.responsiblePersonDetails.address),
        );

        const path =
          completed && isSameAddress ? '../decision' : `../${ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON}`;

        this.router.navigate([path], { relativeTo: this.activatedRoute });
      });
  }
}

function updateOperatorAddress(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<AccountAddressFormModel>,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.operatorAddress = form.getRawValue();
  });
}

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  isTargetUnitDetailsWizardCompleted,
  OPERATOR_ADDRESS_FORM,
  OperatorAddressFormProvider,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { AccountAddressFormModel } from '@shared/components';
import { produce } from 'immer';

import {
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';

@Component({
  selector: 'cca-variation-operator-address',
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
  imports: [ReactiveFormsModule, WizardStepComponent, AccountAddressInputComponent, ReturnToTaskOrActionPageComponent],
  providers: [OperatorAddressFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OperatorAddressComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<AccountAddressFormModel>>(OPERATOR_ADDRESS_FORM);

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const updatedPayload = updateOperatorAddress(actionPayload, this.form);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = 'IN_PROGRESS';
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.store);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationSubmitRequestTaskPayload) => {
        const tuDetails = payload.underlyingAgreement.underlyingAgreementTargetUnitDetails;

        const path = isTargetUnitDetailsWizardCompleted(tuDetails)
          ? '../check-your-answers'
          : `../${ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON}`;

        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}

function updateOperatorAddress(
  payload: UnderlyingAgreementVariationApplySavePayload,
  form: FormGroup<AccountAddressFormModel>,
): UnderlyingAgreementVariationApplySavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.operatorAddress = form.getRawValue();
  });
}

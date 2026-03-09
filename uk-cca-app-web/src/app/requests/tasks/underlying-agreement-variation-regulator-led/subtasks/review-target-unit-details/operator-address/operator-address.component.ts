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
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { AccountAddressFormModel, AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationRegulatorLedSavePayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';

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
  providers: [OperatorAddressFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAddressComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<AccountAddressFormModel>>(OPERATOR_ADDRESS_FORM);

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);
    const updatedPayload = updateOperatorAddress(actionPayload, this.form);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.store.select(underlyingAgreementVariationRegulatorLedQuery.selectDetermination)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationRegulatorLedRequestTaskPayload) => {
      const tuDetails = payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails;

      const path = isTargetUnitDetailsWizardCompleted(tuDetails)
        ? '../check-your-answers'
        : `../${ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON}`;

      this.router.navigate([path], { relativeTo: this.route });
    });
  }
}

function updateOperatorAddress(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  form: FormGroup<AccountAddressFormModel>,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.operatorAddress = form.getRawValue();
  });
}

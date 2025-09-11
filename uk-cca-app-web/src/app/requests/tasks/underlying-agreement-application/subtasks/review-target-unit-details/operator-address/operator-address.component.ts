import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  OPERATOR_ADDRESS_FORM,
  OperatorAddressFormProvider,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { AccountAddressFormModel, AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

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
  standalone: true,
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
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = update(actionPayload, this.form);
    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}

function update(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<AccountAddressFormModel>,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.operatorAddress = form.getRawValue();
  });
}

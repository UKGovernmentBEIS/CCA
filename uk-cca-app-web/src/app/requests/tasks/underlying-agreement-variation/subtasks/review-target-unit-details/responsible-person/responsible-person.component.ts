import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  RESPONSIBLE_PERSON_FORM,
  ResponsiblePersonFormProvider,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { ResponsiblePersonInputComponent, WizardStepComponent } from '@shared/components';
import { ResponsiblePersonFormModel } from '@shared/components';
import { produce } from 'immer';

import {
  UnderlyingAgreementVariationApplySavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';

@Component({
  selector: 'cca-variation-responsible-person',
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
    ReactiveFormsModule,
    WizardStepComponent,
    ResponsiblePersonInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ResponsiblePersonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ResponsiblePersonComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<ResponsiblePersonFormModel>>(RESPONSIBLE_PERSON_FORM);

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);
    const updatedPayload = updateResponsiblePerson(actionPayload, this.form);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.store);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}

function updateResponsiblePerson(
  payload: UnderlyingAgreementVariationApplySavePayload,
  form: FormGroup<ResponsiblePersonFormModel>,
): UnderlyingAgreementVariationApplySavePayload {
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

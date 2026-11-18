import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  RESPONSIBLE_PERSON_FORM,
  ResponsiblePersonFormProvider,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { ResponsiblePersonFormModel, ResponsiblePersonInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../transform';
import { applySaveActionSideEffects } from '../../../utils';

@Component({
  selector: 'cca-responsible-person',
  template: `
    <cca-wizard-step (formSubmit)="onSubmit()" [formGroup]="form" caption="Change" heading="Responsible person">
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
export class ResponsiblePersonComponent {
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FormGroup<ResponsiblePersonFormModel>>(RESPONSIBLE_PERSON_FORM);

  onSubmit() {
    const payload = this.store.select(requestTaskQuery.selectRequestTaskPayload)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);
    const updatedPayload = updateResponsiblePerson(actionPayload, this.form);

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

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}

function updateResponsiblePerson(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<ResponsiblePersonFormModel>,
): UnderlyingAgreementApplySavePayload {
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

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TargetUnitDetailsSubmitFormProvider,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { OperatorTypePipe } from '@shared/pipes';
import { textFieldValidators } from '@shared/validators';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';

@Component({
  selector: 'cca-variation-target-unit-details',
  templateUrl: './target-unit-details.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    TextInputComponent,
    ReturnToTaskOrActionPageComponent,
    OperatorTypePipe,
  ],
  providers: [TargetUnitDetailsSubmitFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class TargetUnitDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly targetUnitDetails = this.store.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  )();

  protected readonly form = new FormGroup({
    operatorName: new FormControl(this.targetUnitDetails?.operatorName ?? null, textFieldValidators('operator name')),
  });

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Update the target unit details with form data
    const updatedPayload = produce(actionPayload, (draft) => {
      draft.underlyingAgreementTargetUnitDetails.operatorName = this.form.getRawValue().operatorName;
    });

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.store);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

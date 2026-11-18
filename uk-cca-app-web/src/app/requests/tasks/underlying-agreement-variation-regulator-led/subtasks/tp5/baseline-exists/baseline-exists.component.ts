import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { NotificationBannerComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import {
  applyTp5ExistSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  isTargetPeriodWizardCompleted,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import {
  BASELINE_EXISTS_FORM,
  BaselineExistsFormModel,
  BaselineExistsFormProvider,
} from './baseline-exists-form.provider';

@Component({
  selector: 'cca-baseline-exists',
  templateUrl: './baseline-exists.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioOptionComponent,
    RadioComponent,
    ReturnToTaskOrActionPageComponent,
    NotificationBannerComponent,
  ],
  providers: [BaselineExistsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineExistsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<BaselineExistsFormModel>(BASELINE_EXISTS_FORM);

  protected readonly showNotificationBanner = this.router.url.includes('underlying-agreement-application');

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);

    let updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod5Details.exist = this.form.value.exist;
    });

    updatedPayload = applyTp5ExistSideEffect(updatedPayload);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

      const targetPeriodDetails = this.requestTaskStore.select(
        underlyingAgreementQuery.selectTargetPeriodDetails(true),
      )();

      const completed = baselineExists === false || isTargetPeriodWizardCompleted(targetPeriodDetails);
      if (completed) {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
      } else {
        this.router.navigate([`../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`], { relativeTo: this.route });
      }
    });
  }
}

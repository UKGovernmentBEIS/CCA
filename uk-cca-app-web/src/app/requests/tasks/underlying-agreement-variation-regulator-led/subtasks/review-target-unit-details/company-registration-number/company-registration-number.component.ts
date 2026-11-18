import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CommonCompanyRegistrationNumberComponent,
  CompanyRegistrationNumberSubmitEvent,
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
  updateTUDetailsRegulatorLed,
} from '@requests/common';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';

@Component({
  selector: 'cca-company-registration-number',
  template: `<cca-common-company-registration-number (submitted)="onSubmit($event)" />`,
  imports: [CommonCompanyRegistrationNumberComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompanyRegistrationNumberComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  onSubmit(event: CompanyRegistrationNumberSubmitEvent) {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);
    const updatedPayload = updateTUDetailsRegulatorLed(actionPayload, event.companyNumberState, event.companyProfile);
    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.store.select(underlyingAgreementVariationRegulatorLedQuery.selectDetermination)();

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((updatedRequestTaskPayload: UNAVariationRegulatorLedRequestTaskPayload) => {
        const path = isTargetUnitDetailsWizardCompleted(
          updatedRequestTaskPayload.underlyingAgreement?.underlyingAgreementTargetUnitDetails,
        )
          ? '../check-your-answers'
          : `../${ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS}`;

        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}

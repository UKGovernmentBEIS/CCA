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
  underlyingAgreementQuery,
  updateTUDetails,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

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
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateTUDetails(actionPayload, event.companyNumberState, event.companyProfile);
    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((updatedRequestTaskPayload: UnderlyingAgreementSubmitRequestTaskPayload) => {
        const path = isTargetUnitDetailsWizardCompleted(
          updatedRequestTaskPayload.underlyingAgreement?.underlyingAgreementTargetUnitDetails,
        )
          ? '../check-your-answers'
          : `../${ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS}`;

        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}

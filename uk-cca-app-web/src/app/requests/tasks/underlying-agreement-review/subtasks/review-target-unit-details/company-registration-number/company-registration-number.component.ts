import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CommonCompanyRegistrationNumberComponent,
  CompanyRegistrationNumberSubmitEvent,
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  updateTUDetails,
} from '@requests/common';

import { UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../transform';
import { applySaveActionSideEffects } from '../../../utils';

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
    const payload = this.store.select(requestTaskQuery.selectRequestTaskPayload)();
    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);
    const updatedPayload = updateTUDetails(actionPayload, event.companyNumberState, event.companyProfile);

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      determination,
      reviewSectionsCompleted,
      sectionsCompleted,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((updatedRequestTaskPayload: UnderlyingAgreementSubmitRequestTaskPayload) => {
        const path = isTargetUnitDetailsWizardCompleted(
          updatedRequestTaskPayload.underlyingAgreement?.underlyingAgreementTargetUnitDetails,
        )
          ? '../decision'
          : `../${ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS}`;

        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}

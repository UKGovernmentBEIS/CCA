import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  areEntitiesIdentical,
  CommonCompanyRegistrationNumberComponent,
  CompanyRegistrationNumberSubmitEvent,
  filterFieldsWithFalsyValues,
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TasksApiService,
  transformAccountReferenceData,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  updateVariationReviewTUDetails,
} from '@requests/common';

import { UnderlyingAgreementVariationReviewRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { applySaveActionSideEffects, deleteDecision } from '../../../utils';

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
    )() as UNAVariationReviewRequestTaskPayload;

    const originalAccountReferenceData = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.accountReferenceData;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const updatedPayload = updateVariationReviewTUDetails(
      actionPayload,
      event.companyNumberState,
      event.companyProfile,
    );

    const originalTUDetails = transformAccountReferenceData(originalAccountReferenceData);
    const currentTUDetails = updatedPayload.underlyingAgreementTargetUnitDetails;

    const areIdentical = areEntitiesIdentical(
      filterFieldsWithFalsyValues(currentTUDetails),
      filterFieldsWithFalsyValues(originalTUDetails),
    );

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)();
    const decisions = areIdentical ? currentDecisions : deleteDecision(currentDecisions, 'TARGET_UNIT_DETAILS');

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: decisions,
      facilitiesReviewGroupDecisions: this.store.select(
        underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
      )(),
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((updatedRequestTaskPayload: UnderlyingAgreementVariationReviewRequestTaskPayload) => {
        let path = '';

        if (areIdentical) {
          path = '../check-your-answers';
        } else {
          path = isTargetUnitDetailsWizardCompleted(
            updatedRequestTaskPayload.underlyingAgreement?.underlyingAgreementTargetUnitDetails,
          )
            ? '../decision'
            : `../${ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS}`;
        }

        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}

import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  OPERATOR_ASSENT_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toAuthorisationAdditionalEvidenceSummaryData,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  templateUrl: './authorisation-additional-evidence-check-your-answers.component.html',
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toAuthorisationAdditionalEvidenceSummaryData(
    this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectOriginalAuthorisationAndAdditionalEvidence,
    )(),
    this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectOriginalUnderlyingAgreementAttachments,
    )(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryData(
    this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectAuthorisationAndAdditionalEvidence,
    )(),
    this.requestTaskStore.select(underlyingAgreementVariationRegulatorLedQuery.selectUnderlyingAgreementAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(
        requestTaskQuery.selectRequestTaskPayload,
      )() as UNAVariationRegulatorLedRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);

    const currentAdditionalEvidence = actionPayload.authorisationAndAdditionalEvidence;
    const originalAdditionalEvidence = originalPayload?.underlyingAgreement?.authorisationAndAdditionalEvidence;
    const areIdentical = areEntitiesIdentical(currentAdditionalEvidence, originalAdditionalEvidence);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = areIdentical
        ? TaskItemStatus.UNCHANGED
        : TaskItemStatus.COMPLETED;

      draft[OPERATOR_ASSENT_DECISION_SUBTASK] =
        draft[OPERATOR_ASSENT_DECISION_SUBTASK] !== TaskItemStatus.COMPLETED
          ? draft[OPERATOR_ASSENT_DECISION_SUBTASK]
          : TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}

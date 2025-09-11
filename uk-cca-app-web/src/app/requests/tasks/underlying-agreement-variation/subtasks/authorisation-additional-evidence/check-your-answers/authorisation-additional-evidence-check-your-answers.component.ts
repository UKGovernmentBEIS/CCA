import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toAuthorisationAdditionalEvidenceSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps, resetReviewSection } from '../../../utils';

@Component({
  selector: 'cca-check-your-answers',
  templateUrl: './authorisation-additional-evidence-check-your-answers.component.html',
  standalone: true,
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
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const reviewProps = extractReviewProps(this.requestTaskStore);
    const resetedProps = resetReviewSection(reviewProps, AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted, {
      ...reviewProps,
      ...resetedProps,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}

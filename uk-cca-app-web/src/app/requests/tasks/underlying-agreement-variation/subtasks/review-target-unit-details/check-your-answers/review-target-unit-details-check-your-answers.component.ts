import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toVariationTargetUnitDetailsOriginalSummaryData,
  toVariationTargetUnitDetailsSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps, resetReviewSection } from '../../../utils';

@Component({
  selector: 'cca-check-your-answers',
  templateUrl: './review-target-unit-details-check-your-answers.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly summaryDataOriginal = toVariationTargetUnitDetailsOriginalSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  protected readonly summaryDataCurrent = toVariationTargetUnitDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    // after any task is completed, we reset the review section to undecided and remove the decision
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const resetedPropes = resetReviewSection(reviewProps, REVIEW_TARGET_UNIT_DETAILS_SUBTASK);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted, {
      ...reviewProps,
      ...resetedPropes,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}

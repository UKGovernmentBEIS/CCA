import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  BaselineAndTargetPeriodsSubtasks,
  OPERATOR_ASSENT_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toBaselineAndTargetsSummaryData,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';

@Component({
  selector: 'cca-tp5-check-your-answers',
  templateUrl: './tp5-check-your-answers.component.html',
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
export class Tp5CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly sectorAssociationDetailsSchemeData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
  )();

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  private readonly multipleFilesDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toBaselineAndTargetsSummaryData(
    true, // TP5
    this.requestTaskStore.select(underlyingAgreementVariationRegulatorLedQuery.selectOriginalBaselineExists)(),
    this.sectorAssociationDetailsSchemeData,
    this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectOriginalTargetPeriodDetails(true),
    )(),
    this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectOriginalUnderlyingAgreementAttachments,
    )(),
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryData(
    true, // TP5
    this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)(),
    this.sectorAssociationDetailsSchemeData,
    this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
    this.isEditable,
    this.multipleFilesDownloadUrl,
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

    const currentTP5 = actionPayload.targetPeriod5Details;
    const originalTP5 = originalPayload?.underlyingAgreement?.targetPeriod5Details;
    const areIdentical = areEntitiesIdentical(currentTP5, originalTP5);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = areIdentical
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

import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  toBaselineAndTargetsSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, PageHeadingComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

@Component({
  selector: 'cca-baseline-and-targets-summary',
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  templateUrl: './baseline-and-targets-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineAndTargetsSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly isTargetPeriod5 =
    this.baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  private readonly sectorAssociationDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceDataSectorAssociationDetails,
  )();

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  private readonly multipleFilesDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toBaselineAndTargetsSummaryData(
    this.isTargetPeriod5,
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalBaselineExists)(),
    this.sectorAssociationDetails,
    this.requestTaskStore.select(
      underlyingAgreementVariationQuery.selectOriginalTargetPeriodDetails(this.isTargetPeriod5),
    )(),
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryData(
    this.isTargetPeriod5,
    this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)(),
    this.sectorAssociationDetails,
    this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodDetails(this.isTargetPeriod5))(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );
}

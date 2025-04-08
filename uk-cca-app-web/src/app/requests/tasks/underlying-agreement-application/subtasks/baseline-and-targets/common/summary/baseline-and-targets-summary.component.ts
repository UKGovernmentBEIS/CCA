import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  toBaselineAndTargetsSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-baseline-and-targets-summary',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './baseline-and-targets-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineAndTargetsSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly isTargetPeriod5 =
    this.baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

  protected readonly baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  protected readonly sectorAssociationDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceDataSectorAssociationDetails,
  )();

  protected readonly targetPeriodDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(this.isTargetPeriod5),
  )();

  protected attachments = this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

  protected isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  protected readonly multipleFilesDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toBaselineAndTargetsSummaryData(
    this.isTargetPeriod5,
    this.baselineExists,
    this.sectorAssociationDetails,
    this.targetPeriodDetails,
    this.attachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );
}

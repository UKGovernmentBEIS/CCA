import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  toBaselineAndTargetsSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

@Component({
  selector: 'cca-check-your-answers',
  standalone: true,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './baseline-and-targets-check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineAndTargetsCheckYourAnswersComponent {
  private readonly baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);
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
  onSubmit() {
    this.taskService
      .submitSubtask(
        this.isTargetPeriod5
          ? BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS
          : BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS,
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}

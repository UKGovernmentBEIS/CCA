import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  BaselineAndTargetPeriodsSubtasks,
  toBaselineAndTargetsSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

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
  template: `
    <div>
      <netz-page-heading caption="TP6 (2024)">Check your answers</netz-page-heading>

      <cca-summary [data]="summaryData" />

      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP6CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);
  private readonly store = inject(RequestTaskStore);
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
  )();

  protected readonly sectorAssociationDetails = this.store.select(
    underlyingAgreementQuery.selectAccountReferenceDataSectorAssociationDetails,
  )();

  protected readonly targetPeriodDetails = this.store.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(false),
  )();
  protected submitAttachments = this.store.select(underlyingAgreementQuery.selectAttachments)();
  protected reviewAttachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  protected isEditable = this.store.select(requestTaskQuery.selectIsEditable)();

  private readonly summaryMetadata = {
    isTp5Period: false,
    baselineExists: null,
    downloadUrl: generateDownloadUrl(this.taskId),
    isEditable: this.isEditable,
    attachments: { submit: this.submitAttachments, review: this.reviewAttachments },
  };

  protected readonly summaryData = toBaselineAndTargetsSummaryDataWithDecision(
    this.sectorAssociationDetails,
    this.targetPeriodDetails,
    this.decision,
    this.summaryMetadata,
  );
  onSubmit() {
    this.taskService
      .submitSubtask(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}

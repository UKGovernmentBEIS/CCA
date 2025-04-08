import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { toOverallDecisionSummaryData, underlyingAgreementReviewQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-overall-decision-check-your-answers',
  standalone: true,
  template: `
    <div>
      <netz-page-heading [caption]="caption">Check your answers</netz-page-heading>

      <cca-summary [data]="summaryData" />

      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
})
export class OverallDecisionCheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  private readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  protected readonly summaryData = toOverallDecisionSummaryData(
    this.determination,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  onSubmit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .submitReviewDetermination(this.determination)
      .subscribe(() => this.router.navigate(['../../../'], { relativeTo: this.activatedRoute }));
  }
}

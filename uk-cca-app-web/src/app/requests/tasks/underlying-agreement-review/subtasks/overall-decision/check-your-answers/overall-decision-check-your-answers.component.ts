import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { tap } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { underlyingAgreementReviewQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { UnderlyingAgreementReviewTaskService } from '../../../services/underlying-agreement-review-task.service';
import { OverallDecisionStore } from '../overall-decision.store';
import { toOverallDecisionSummaryData } from '../to-overall-decision-summary-data';

@Component({
  selector: 'cca-overall-decision-check-your-answers',
  standalone: true,
  template: `
    <div>
      <netz-page-heading [caption]="caption">Check your answers</netz-page-heading>

      <cca-summary [data]="summaryData" />

      <button ccaPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
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
  private readonly overallDecisionStore = inject(OverallDecisionStore);
  private readonly taskService = inject(TaskService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly determination = this.overallDecisionStore.determination;
  readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
  readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );
  readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';
  readonly summaryData = toOverallDecisionSummaryData(
    this.determination,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  onSubmit() {
    (this.taskService as UnderlyingAgreementReviewTaskService)
      .saveReviewDetermination(this.determination)
      .pipe(
        tap(() => {
          this.router.navigate(['../../../'], { relativeTo: this.route });
        }),
      )
      .subscribe();
  }
}

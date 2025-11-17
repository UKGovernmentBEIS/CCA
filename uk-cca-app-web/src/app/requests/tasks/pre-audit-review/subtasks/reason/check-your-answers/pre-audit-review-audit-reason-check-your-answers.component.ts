import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { produce } from 'immer';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK } from '../../../types';
import { toPreAuditReviewAuditReasonSummaryData } from '../pre-audit-review-audit-reason-summary-data';

@Component({
  selector: 'cca-pre-audit-review-audit-reason-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Audit reason">Check your answers</netz-page-heading>
      <cca-summary [data]="data()" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    PageHeadingComponent,
    ReturnToTaskOrActionPageComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewAuditReasonCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);

  private readonly auditReasonDetails = this.requestTaskStore.select(preAuditReviewQuery.selectPreAuditReviewDetails);

  protected readonly data = computed(() =>
    toPreAuditReviewAuditReasonSummaryData(
      this.auditReasonDetails()?.auditReasonDetails,
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    ),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(preAuditReviewQuery.selectPayload)();

    const currentSectionsCompleted = this.requestTaskStore.select(preAuditReviewQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute });
    });
  }
}

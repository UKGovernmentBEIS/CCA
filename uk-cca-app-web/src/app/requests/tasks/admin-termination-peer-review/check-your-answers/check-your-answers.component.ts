import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { AdminTerminationPeerReviewRequestTaskPayload, TasksService } from 'cca-api';

import { AdminTerminationPeerReviewStore } from '../+state';
import { peerReviewDecisionToSummaryData } from './peer-review-decision-to-summary-data';

@Component({
  selector: 'cca-peer-review-decision-check-your-answers',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-two-thirds">
      <h1 class="govuk-heading-l">Check your answers</h1>

      <cca-summary [data]="summaryData()" />

      <button govukButton type="button" (click)="onSubmit()" class="govuk-!-margin-top-6">Confirm and complete</button>

      <p class="govuk-body govuk-!-margin-top-6">
        <a routerLink="../../" class="govuk-link">Return to: Peer review admin termination request</a>
      </p>
    </div>
  </div>`,
  standalone: true,
  imports: [SummaryComponent, ButtonDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly peerReviewStore = inject(AdminTerminationPeerReviewStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksService = inject(TasksService);

  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
  private readonly downloadUrl = generateDownloadUrl(this.taskId.toString());

  protected readonly summaryData = computed(() => {
    const state = this.peerReviewStore.state;
    const decision = state.decision;
    const attachments = state.attachments;

    if (!decision) {
      return [];
    }

    const summaryDataWithUrl = peerReviewDecisionToSummaryData(decision, attachments);

    return summaryDataWithUrl;
  });

  onSubmit() {
    const state = this.peerReviewStore.state;
    const decision = state.decision;

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'ADMIN_TERMINATION_SUBMIT_PEER_REVIEW_DECISION',
        requestTaskId: +this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'ADMIN_TERMINATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD',
          decision: decision,
        } as AdminTerminationPeerReviewRequestTaskPayload,
      })
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { peerReviewDecisionToSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { CcaPeerReviewDecisionRequestTaskActionPayload, TasksService } from 'cca-api';

import { NoticeOfIntentPeerReviewStore } from '../+state';

@Component({
  selector: 'cca-peer-review-decision-check-your-answers',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-two-thirds">
      <h1 class="govuk-heading-l">Check your answers</h1>

      <cca-summary [data]="summaryData()" />

      <button govukButton type="button" (click)="onSubmit()" class="govuk-!-margin-top-6">Confirm and complete</button>

      <p class="govuk-body govuk-!-margin-top-6">
        <a routerLink="../../../" class="govuk-link">Return to: Peer review notice of intent</a>
      </p>
    </div>
  </div>`,
  imports: [SummaryComponent, ButtonDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly peerReviewStore = inject(NoticeOfIntentPeerReviewStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksService = inject(TasksService);
  private readonly state = this.peerReviewStore.stateAsSignal;
  private readonly requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);

  protected readonly summaryData = computed(() => {
    const state = this.state();
    const decision = state.decision;
    const attachments = state.attachments;

    if (!decision) {
      return [];
    }

    return peerReviewDecisionToSummaryData(decision, attachments);
  });

  onSubmit() {
    const state = this.state();
    const decision = state.decision;

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION',
        requestTaskId: +this.requestTaskId(),
        requestTaskActionPayload: {
          payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_DECISION_PAYLOAD',
          decision: decision,
          referencedAttachmentIds: decision?.files ?? [],
        } as CcaPeerReviewDecisionRequestTaskActionPayload,
      })
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}

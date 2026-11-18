import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { peerReviewDecisionToSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { CcaPeerReviewDecisionRequestTaskActionPayload, TasksService } from 'cca-api';

import { EnforcementResponseNoticePeerReviewStore } from '../+state';

@Component({
  selector: 'cca-peer-review-decision-check-your-answers',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <netz-page-heading>Check your answers</netz-page-heading>

        <cca-summary [data]="summaryData()" />

        <button govukButton type="button" (click)="onSubmit()" class="govuk-!-margin-top-6">
          Confirm and complete
        </button>
      </div>
    </div>
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, ButtonDirective, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly peerReviewStore = inject(EnforcementResponseNoticePeerReviewStore);
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
        requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PEER_REVIEW_DECISION',
        requestTaskId: +this.requestTaskId(),
        requestTaskActionPayload: {
          payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_DECISION_PAYLOAD',
          decision,
        } as CcaPeerReviewDecisionRequestTaskActionPayload,
      })
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}

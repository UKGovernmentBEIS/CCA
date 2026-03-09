import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, throwError } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { CcaPeerReviewDecisionRequestTaskActionPayload, TasksService } from 'cca-api';

import { UnARegulatorLedVariationPeerReviewStore } from '../+state/underlying-agreement-variation-regulator-led-peer-review.store';
import { peerReviewDecisionToSummaryData } from './peer-review-decision-to-summary-data';

@Component({
  selector: 'cca-peer-review-decision-check-your-answers',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <h1 class="govuk-heading-l">Check your answers</h1>

        <cca-summary [data]="summaryData()" />

        <button govukButton type="button" (click)="onSubmit()" class="govuk-!-margin-top-6">
          Confirm and complete
        </button>
      </div>
    </div>

    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, ButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly peerReviewStore = inject(UnARegulatorLedVariationPeerReviewStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksService = inject(TasksService);

  protected readonly summaryData = computed(() => {
    const state = this.peerReviewStore.state;
    const decision = state.decision;
    const attachments = state.attachments;

    return peerReviewDecisionToSummaryData(decision, attachments);
  });

  onSubmit() {
    const state = this.peerReviewStore.state;
    const decision = state.decision;

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PEER_REVIEW_DECISION',
        requestTaskId: +this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD',
          decision: decision,
        } as CcaPeerReviewDecisionRequestTaskActionPayload,
      })
      .pipe(
        catchError((error) => {
          console.error('Error processing request task action:', error);
          return throwError(() => error);
        }),
      )
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }
}

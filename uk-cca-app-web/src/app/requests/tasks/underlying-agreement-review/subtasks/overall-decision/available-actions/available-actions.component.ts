import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { OverallDecisionWizardStep } from '@requests/common';

import { underlyingAgreementReviewQuery } from '../../../+state/una-review.selectors';
import { OverallDecisionStore } from '../overall-decision.store';

@Component({
  selector: 'cca-underlying-agreement-available-actions',
  template: `
    <div class="govuk-width-container">
      <netz-page-heading>Overall decision</netz-page-heading>
      <h2 class="govuk-heading-m">Available actions</h2>
      <p class="govuk-body">Based on the current status of the determination</p>
      <div>
        @if (canAccept) {
          <button govukButton class="govuk-!-margin-right-2" type="button" (click)="submit('ACCEPTED')">Accept</button>
        }
        @if (canReject) {
          <button govukButton type="button" (click)="submit('REJECTED')">Reject</button>
        }
      </div>
    </div>
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, ButtonDirective, PendingButtonDirective, ReturnToTaskOrActionPageComponent],
})
export class AvailableActionsComponent {
  overallDecisionStore = inject(OverallDecisionStore);
  store = inject(RequestTaskStore);

  router = inject(Router);
  route = inject(ActivatedRoute);
  taskService = inject(TaskService);
  canReject = this.store.select(underlyingAgreementReviewQuery.selectCanReject)();
  canAccept = this.store.select(underlyingAgreementReviewQuery.selectCanAccept)();

  submit(type: 'ACCEPTED' | 'REJECTED') {
    this.overallDecisionStore.updateDetermination({ type });
    this.router.navigate(
      ['../', type === 'ACCEPTED' ? OverallDecisionWizardStep.ADDITIONAL_INFO : OverallDecisionWizardStep.EXPLANATION],
      { relativeTo: this.route, queryParamsHandling: 'preserve' },
    );
  }
}

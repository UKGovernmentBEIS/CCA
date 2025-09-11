import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { OverallDecisionWizardStep, TasksApiService, underlyingAgreementReviewQuery } from '@requests/common';

import { createSaveDeterminationActionDTO } from '../../../transform';
import { underlyingAgreementReviewTaskQuery } from '../../../una-review.selectors';
import { resetDeterminationStatus } from '../../../utils';

@Component({
  selector: 'cca-underlying-agreement-available-actions',
  template: `
    <div class="govuk-width-container">
      <netz-page-heading>Overall decision</netz-page-heading>
      <h2 class="govuk-heading-m">Available actions</h2>
      <p>Based on the current status of the determination</p>
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
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, ButtonDirective, ReturnToTaskOrActionPageComponent],
})
export class AvailableActionsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly taskApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  protected readonly canReject = this.requestTaskStore.select(underlyingAgreementReviewTaskQuery.selectCanReject)();
  protected readonly canAccept = this.requestTaskStore.select(underlyingAgreementReviewTaskQuery.selectCanAccept)();

  submit(type: 'ACCEPTED' | 'REJECTED') {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const currReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = resetDeterminationStatus(currReviewSectionsCompleted);

    const payload = createSaveDeterminationActionDTO(requestTaskId, { type }, reviewSectionsCompleted);

    this.taskApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(
        [
          '../',
          type === 'ACCEPTED' ? OverallDecisionWizardStep.ADDITIONAL_INFO : OverallDecisionWizardStep.EXPLANATION,
        ],
        {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'preserve',
        },
      );
    });
  }
}

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toOverallDecisionSummaryData,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveDeterminationActionDTO } from '../../../transform';

@Component({
  selector: 'cca-overall-decision-check-your-answers',
  template: `
    <div>
      <netz-page-heading [caption]="caption">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
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
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly determination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
  private readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable)();
  private readonly attachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  protected readonly summaryData = toOverallDecisionSummaryData(
    this.determination,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const currReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const determination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();

    const reviewSectionsCompleted = produce(currReviewSectionsCompleted, (draft) => {
      draft[OVERALL_DECISION_SUBTASK] =
        determination.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
    });

    const payload = createSaveDeterminationActionDTO(requestTaskId, determination, reviewSectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(['../../../'], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'preserve',
      });
    });
  }
}

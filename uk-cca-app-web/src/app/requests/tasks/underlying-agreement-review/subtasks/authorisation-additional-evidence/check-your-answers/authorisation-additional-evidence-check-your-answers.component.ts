import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toAuthorisationAdditionalEvidenceSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveDecisionActionDTO } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Authorisation and additional evidence">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceCheckYourAnswersComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
    this.store.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
    this.store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'))(),
    this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  );

  onSubmit() {
    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const decision = this.store.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] =
        decision.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
    });

    const determination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute });
    });
  }
}

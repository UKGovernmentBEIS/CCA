import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  OVERALL_DECISION_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toReviewTargetUnitDetailsUNAReviewSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { UnderlyingAgreementReviewDecision } from 'cca-api';

import { createSaveDecisionActionDTO } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <cca-wizard-step [formGroup]="form" (formSubmit)="submit()">
        <cca-decision />
      </cca-wizard-step>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    DecisionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [decisionFormProvider('TARGET_UNIT_DETAILS')],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  protected readonly summaryData = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  submit() {
    {
      const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

      const decision: UnderlyingAgreementReviewDecision = {
        type: this.form.value.type,
        details: {
          notes: this.form.value.notes,
          files: this.form.value.files.map((file) => file.uuid),
        },
      };

      const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
      const determination = resetDetermination(currDetermination);

      const currentReviewSectionsCompleted = this.store.select(
        underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
      )();

      const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
        draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.UNDECIDED;
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      });

      const payload = createSaveDecisionActionDTO(
        requestTaskId,
        'TARGET_UNIT_DETAILS',
        reviewSectionsCompleted,
        decision,
        determination,
      );

      this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
        this.router.navigate(['../', 'check-your-answers'], { relativeTo: this.activatedRoute });
      });
    }
  }
}

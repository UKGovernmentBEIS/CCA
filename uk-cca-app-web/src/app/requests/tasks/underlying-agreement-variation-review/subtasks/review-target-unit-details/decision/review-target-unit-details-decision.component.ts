import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  decisionFormProvider,
  OVERALL_DECISION_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toReviewTargetUnitDetailsUNAReviewSummaryData,
  transform,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { createSaveDecisionActionDTO } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>

      <ng-template #contentTpl let-showOriginal="showOriginal">
        <cca-summary [data]="showOriginal ? summaryDataOriginal : summaryDataCurrent" />
      </ng-template>

      <cca-highlight-diff>
        <ng-container slot="previous" *ngTemplateOutlet="contentTpl; context: { showOriginal: true }" />
        <ng-container slot="current" *ngTemplateOutlet="contentTpl; context: { showOriginal: false }" />
      </cca-highlight-diff>

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
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  providers: [decisionFormProvider('TARGET_UNIT_DETAILS')],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsDecisionComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject(DECISION_FORM_PROVIDER);

  private readonly accountReferenceData = this.store.select(underlyingAgreementQuery.selectAccountReferenceData);

  private readonly originalTargetUnitDetails = transform(this.accountReferenceData());

  protected readonly summaryDataOriginal = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.originalTargetUnitDetails,
    this.store.select(requestTaskQuery.selectIsEditable)(),
  );

  protected readonly summaryDataCurrent = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
  );

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.UNDECIDED;
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());

    const decision = {
      type: this.form.value.type!,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files?.map((f: any) => f.uuid) || [],
      },
    };

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'TARGET_UNIT_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

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
  TaskItemStatus,
  TasksApiService,
  toVariationDetailsSummaryData,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { resetDetermination } from 'src/app/requests/tasks/underlying-agreement-review/utils';

import { createSaveDecisionActionDTO } from '../../../transform';

@Component({
  selector: 'cca-variation-details-decision',
  template: `
    <div>
      <netz-page-heading>Variation details</netz-page-heading>
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
    ReactiveFormsModule,
    DecisionComponent,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [decisionFormProvider('VARIATION_DETAILS')],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsDecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toVariationDetailsSummaryData(
    this.store.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
  );

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[VARIATION_DETAILS_SUBTASK] = TaskItemStatus.UNDECIDED;
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());

    const decision = {
      type: this.form.value.type!,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files?.map((f) => f.uuid) || [],
      },
    };

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'VARIATION_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

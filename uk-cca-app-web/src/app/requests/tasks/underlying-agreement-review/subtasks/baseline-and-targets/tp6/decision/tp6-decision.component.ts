import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  toBaselineAndTargetsSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { UnderlyingAgreementReviewTaskService } from '../../../../services/underlying-agreement-review-task.service';

@Component({
  selector: 'cca-baseline-and-targets-summary',
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    DecisionComponent,
    ReactiveFormsModule,
    ButtonDirective,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [decisionFormProvider('TARGET_PERIOD6_DETAILS')],
  template: `
    <div>
      <cca-page-heading caption="Baseline and targets">TP6(2024)</cca-page-heading>

      <cca-summary [data]="summaryData" />
      <cca-wizard-step [formGroup]="form" (formSubmit)="submit()">
        <cca-decision></cca-decision>
      </cca-wizard-step>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP6DecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  protected readonly baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  protected readonly sectorAssociationDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceDataSectorAssociationDetails,
  )();

  protected readonly targetPeriodDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(false),
  )();

  protected attachments = this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

  protected isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  protected readonly multipleFilesDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toBaselineAndTargetsSummaryData(
    false,
    this.baselineExists,
    this.sectorAssociationDetails,
    this.targetPeriodDetails,
    this.attachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );
  submit() {
    (this.taskService as UnderlyingAgreementReviewTaskService)
      .saveDecision(this.form.value, 'TARGET_PERIOD6_DETAILS', BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS)
      .subscribe(() => {
        this.router.navigate(['../', BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.route,
        });
      });
  }
}

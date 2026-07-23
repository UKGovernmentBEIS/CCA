import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl, logger } from '@shared/utils';

import { isAppealOutcomeCompleted } from '../appeal-outcome.guard';
import { appealOutcomeQuery } from '../appeal-outcome.selectors';
import { toAppealOutcomeSummaryData } from '../to-appeal-outcome-summary-data';
import { createCompleteActionDTO } from '../transform';

@Component({
  selector: 'cca-appeal-outcome-check-your-answers',
  template: `
    <netz-page-heading caption="Appeal outcome details">Check your answers</netz-page-heading>
    <cca-summary [data]="summaryData()" />
    @if (isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    }
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly appealOutcome = this.requestTaskStore.select(appealOutcomeQuery.selectAppealOutcome);
  private readonly attachments = this.requestTaskStore.select(appealOutcomeQuery.selectNonComplianceAttachments);
  private readonly requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly summaryData = computed(() =>
    toAppealOutcomeSummaryData(
      this.appealOutcome(),
      this.attachments() ?? {},
      this.isEditable(),
      generateDownloadUrl(this.requestTaskId()?.toString()),
    ),
  );

  onSubmit() {
    if (!this.isEditable()) {
      return;
    }

    if (!isAppealOutcomeCompleted(this.appealOutcome())) {
      this.router.navigate(['../provide-details'], { relativeTo: this.activatedRoute });
      return;
    }

    const dto = createCompleteActionDTO(this.requestTaskId());

    this.tasksApiService.saveRequestTaskAction(dto).subscribe({
      next: () => {
        this.router.navigate(['../confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
      },
      error: (err: unknown) => {
        logger.error('Failed to complete appeal outcome:', err);
      },
    });
  }
}

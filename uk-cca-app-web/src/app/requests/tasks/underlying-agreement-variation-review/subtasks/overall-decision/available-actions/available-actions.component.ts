import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';
import { OverallDecisionWizardStep, underlyingAgreementReviewQuery } from '@requests/common';

import { underlyingAgreementVariationReviewTaskQuery } from '../../../+state/una-variation-review.selectors';
import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-underlying-agreement-available-actions',
  templateUrl: './available-actions.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, ButtonDirective, WarningTextComponent, ReturnToTaskOrActionPageComponent],
})
export class AvailableActionsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);

  protected readonly canReject = this.requestTaskStore.select(
    underlyingAgreementVariationReviewTaskQuery.selectCanReject,
  )();

  protected readonly canAccept = this.requestTaskStore.select(
    underlyingAgreementVariationReviewTaskQuery.selectCanAccept,
  )();

  protected readonly rejectionWarning = this.requestTaskStore.select(
    underlyingAgreementVariationReviewTaskQuery.selectRejectionWarning,
  )();

  protected readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  submit(type: 'ACCEPTED' | 'REJECTED') {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveReviewDetermination({ type })
      .subscribe(() =>
        this.router.navigate(
          [
            '../',
            type === 'ACCEPTED' ? OverallDecisionWizardStep.ADDITIONAL_INFO : OverallDecisionWizardStep.EXPLANATION,
          ],
          { relativeTo: this.activatedRoute, queryParamsHandling: 'preserve' },
        ),
      );
  }
}

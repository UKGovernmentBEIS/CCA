import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DECISION_FORM_PROVIDER,
  DecisionWithDateComponent,
  DecisionWithDateFormModel,
  facilityDecisionFormProvider,
  FacilityWizardReviewStep,
  toFacilitySummaryDataWithStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-facility-decision',
  templateUrl: './facility-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ReactiveFormsModule,
    DecisionWithDateComponent,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  providers: [facilityDecisionFormProvider()],
  standalone: true,
})
export class FacilityDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);

  protected readonly form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId));

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.facility().status === 'NEW'
        ? this.facility()
        : this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
      this.facility().status === 'NEW'
        ? this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)()
        : this.requestTaskStore.select(
            underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
          )(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );

  protected readonly summaryDataCurrent = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.facility(),
      this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );

  submit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveFacilityDecision(this.form.value, this.facility())
      .subscribe(() => {
        this.router.navigate(['../', FacilityWizardReviewStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.activatedRoute,
        });
      });
  }
}

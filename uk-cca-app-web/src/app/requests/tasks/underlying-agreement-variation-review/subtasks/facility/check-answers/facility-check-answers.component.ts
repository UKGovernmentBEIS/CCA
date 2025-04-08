import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  FACILITIES_SUBTASK,
  toFacilitySummaryDataWithStatusAndDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-check-answers',
  templateUrl: './facility-check-answers.component.html',
  standalone: true,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilityCheckAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatusAndDecision(
      this.facility().status === 'NEW'
        ? this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))()
        : this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
      this.decision,
      {
        submit:
          this.facility().status === 'NEW'
            ? this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)()
            : this.requestTaskStore.select(
                underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
              )(),
        review: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  protected readonly summaryDataCurrent = computed(() =>
    toFacilitySummaryDataWithStatusAndDecision(
      this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
      this.decision,
      {
        submit: this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
        review: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  onSubmit() {
    this.taskService
      .submitSubtask(FACILITIES_SUBTASK)
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.activatedRoute }));
  }
}

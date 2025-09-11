import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  TaskItemStatus,
  TasksApiService,
  toFacilitySummaryDataWithStatusAndDecision,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';

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
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatusAndDecision(
      this.facility().status === 'NEW'
        ? this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))()
        : this.store.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
      this.decision,
      {
        submit:
          this.facility().status === 'NEW'
            ? this.store.select(underlyingAgreementQuery.selectAttachments)()
            : this.store.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
        review: this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.store.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  protected readonly summaryDataCurrent = computed(() =>
    toFacilitySummaryDataWithStatusAndDecision(
      this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
      this.decision,
      {
        submit: this.store.select(underlyingAgreementQuery.selectAttachments)(),
        review: this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.store.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const sectionsCompleted = produce(
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      (draft) => {
        draft[this.facilityId] = TaskItemStatus.COMPLETED;
      },
    );

    const decision = this.store.select(underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId))();

    // Update review sections completed
    const reviewSectionsCompleted = produce(payload.reviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = decision.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());

    const dto = createSaveActionDTO(requestTaskId, actionPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../../..'], { relativeTo: this.activatedRoute });
    });
  }
}

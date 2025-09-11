import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toFacilitySummaryDataWithStatusAndDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-summary',
  templateUrl: './facility-summary.component.html',
  standalone: true,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();

  protected readonly facility = this.requestTaskStore.select(
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId)),
  )();

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatusAndDecision(
      this.facility.status === 'NEW'
        ? this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))()
        : this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
      this.decision,
      {
        submit:
          this.facility.status === 'NEW'
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
}

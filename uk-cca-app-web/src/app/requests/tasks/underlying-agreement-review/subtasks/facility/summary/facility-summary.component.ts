import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toFacilitySummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

@Component({
  selector: 'cca-facility-summary',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  template: `
    @if (facility(); as facility) {
      <div>
        <cca-page-heading>{{ facility.name }} ({{ facility.facilityId }})</cca-page-heading>

        <cca-summary [data]="summaryData()" />
      </div>
    }

    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();
  protected readonly facility = computed(() =>
    this.store
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly summaryData = computed(() =>
    toFacilitySummaryDataWithDecision(
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
}

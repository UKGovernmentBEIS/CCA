import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toFacilitySummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-summary',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading>{{ facility.name }} ({{ facility.facilityId }})</netz-page-heading>

        <cca-summary [data]="summaryData()" />
      </div>
    }

    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly summaryData = computed(() =>
    toFacilitySummaryDataWithDecision(
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

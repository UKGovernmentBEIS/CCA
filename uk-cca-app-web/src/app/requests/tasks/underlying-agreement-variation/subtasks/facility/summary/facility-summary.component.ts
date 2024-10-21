import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toFacilitySummaryDataWithStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, PageHeadingComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

@Component({
  selector: 'cca-facility-summary',
  standalone: true,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  templateUrl: './facility-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
      this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  protected readonly summaryDataCurrent = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
      this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );
}

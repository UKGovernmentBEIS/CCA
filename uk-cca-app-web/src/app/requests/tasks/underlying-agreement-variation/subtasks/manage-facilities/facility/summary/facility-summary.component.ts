import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toFacilitySummaryDataWithStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-summary',
  templateUrl: './facility-summary.component.html',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, HighlightDiffComponent, NgTemplateOutlet, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.facility().status === 'NEW'
        ? this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))()
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
      this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
      this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );
}

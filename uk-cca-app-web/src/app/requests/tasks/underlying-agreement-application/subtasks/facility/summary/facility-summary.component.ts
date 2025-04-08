import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toFacilitySummaryData, underlyingAgreementQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-summary',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
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

  protected readonly summaryData = computed(() =>
    toFacilitySummaryData(
      this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
      this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );
}

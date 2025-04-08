import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toProvideEvidenceSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { underlyingAgreementVariationActivationQuery } from '../../../+state/una-variation-activation.selectors';

@Component({
  selector: 'cca-provide-evidence-summary',
  templateUrl: './provide-evidence-summary.component.html',
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProvideEvidenceSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = computed(() =>
    toProvideEvidenceSummaryData(
      this.requestTaskStore.select(
        underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationDetails,
      )(),
      this.requestTaskStore.select(
        underlyingAgreementVariationActivationQuery.selectUnderlyingAgreementActivationAttachments,
      )(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );
}

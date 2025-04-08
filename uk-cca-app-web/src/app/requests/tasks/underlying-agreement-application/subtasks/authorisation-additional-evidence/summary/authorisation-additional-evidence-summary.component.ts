import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toAuthorisationAdditionalEvidenceSummaryData, underlyingAgreementQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-authorisation-additional-evidence-summary',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './authorisation-additional-evidence-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAuthorisationAdditionalEvidenceSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );
}

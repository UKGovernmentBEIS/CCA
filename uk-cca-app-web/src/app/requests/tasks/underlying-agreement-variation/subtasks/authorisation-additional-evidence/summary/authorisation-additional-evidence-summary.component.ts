import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toAuthorisationAdditionalEvidenceSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-authorisation-additional-evidence-summary',
  templateUrl: './authorisation-additional-evidence-summary.component.html',
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toAuthorisationAdditionalEvidenceSummaryData({
    authorisationAndAdditionalEvidence: this.requestTaskStore.select(
      underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence,
    )(),
    underlyingAgreementAttachments: this.requestTaskStore.select(
      underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
    )(),
    isEditable: this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    downloadUrl: this.downloadUrl,
  });

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryData({
    authorisationAndAdditionalEvidence: this.requestTaskStore.select(
      underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence,
    )(),
    underlyingAgreementAttachments: this.requestTaskStore.select(
      underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments,
    )(),
    isEditable: this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    downloadUrl: this.downloadUrl,
  });
}

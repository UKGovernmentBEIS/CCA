import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  toAuthorisationAdditionalEvidenceSummaryDataWithDecision,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementReviewedRequestActionQuery } from '../../+state/underlying-agreement-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-review-authorisation-additional-evidence',
  template: `
    <div>
      <netz-page-heading>Authorisation and additional evidence</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorisationAdditionalEvidenceComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toAuthorisationAdditionalEvidenceSummaryDataWithDecision({
      authorisationAndAdditionalEvidence: this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectAuthorisationAndAdditionalEvidence,
      )(),
      underlyingAgreementAttachments: this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectAttachments,
      )(),
      isEditable: false,
      downloadUrl: '../../file-download',
      decision: this.requestActionStore.select(
        underlyingAgreementReviewedRequestActionQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
      )(),
      reviewAttachments: this.requestActionStore.select(
        underlyingAgreementReviewedRequestActionQuery.selectReviewAttachments,
      )(),
    }),
  );
}

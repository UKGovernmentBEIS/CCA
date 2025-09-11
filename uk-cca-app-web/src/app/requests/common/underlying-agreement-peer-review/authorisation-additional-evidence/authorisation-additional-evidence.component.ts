import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toAuthorisationAdditionalEvidenceSummaryDataWithDecision } from '../../underlying-agreement/summaries';
import { underlyingAgreementPeerReviewQuery } from '../underlying-agreement-peer-review.selectors';

@Component({
  selector: 'cca-una-peer-review-authorisation-additional-evidence',
  template: `
    <div>
      <netz-page-heading>Authorisation and additional evidence</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>

    <a routerLink="../../" class="govuk-link">Return to: Peer review application for underlying agreement</a>
  `,
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorisationAdditionalEvidenceComponent {
  private readonly store = inject(RequestTaskStore);

  protected readonly summaryData = computed(() => {
    const underlyingAgreement = this.store.select(underlyingAgreementPeerReviewQuery.selectUnderlyingAgreement)();
    return toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
      underlyingAgreement?.authorisationAndAdditionalEvidence,
      this.store.select(underlyingAgreementPeerReviewQuery.selectUnderlyingAgreementAttachments)(),
      false,
      '../../file-download',
      this.store.select(
        underlyingAgreementPeerReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
      )(),
      this.store.select(underlyingAgreementPeerReviewQuery.selectReviewAttachments)(),
    );
  });
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toAuthorisationAdditionalEvidenceSummaryDataWithDecision } from '../../underlying-agreement';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-authorisation-additional-evidence',
  template: `
    <div>
      <netz-page-heading>Authorisation and additional evidence</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
    <a routerLink="../../" class="govuk-link">Return to: Peer review application for underlying agreement variation</a>
  `,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorisationAdditionalEvidenceComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = computed(() =>
    toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectAuthorisationAndAdditionalEvidence,
      )(),
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectUnderlyingAgreementVariationAttachments,
      )(),
      false,
      '../../file-download',
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
      )(),
      this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
    ),
  );
}

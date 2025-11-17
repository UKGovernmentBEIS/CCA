import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toVariationDetailsSummaryDataWithDecision } from '../../underlying-agreement';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-variation-details',
  template: `
    <div>
      <netz-page-heading>Variation details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
    <a routerLink="../../" class="govuk-link">Return to: Peer review application for underlying agreement variation</a>
  `,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = computed(() =>
    toVariationDetailsSummaryDataWithDecision(
      this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectVariationDetails)(),
      false,
      '../../file-download',
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSubtaskDecision('VARIATION_DETAILS'),
      )(),
      this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
    ),
  );
}

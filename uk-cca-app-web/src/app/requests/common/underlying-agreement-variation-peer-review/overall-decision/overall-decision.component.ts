import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { toOverallDecisionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-overall-decision',
  template: `
    <div>
      <netz-page-heading>Overall decision</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
    <a routerLink="../../" class="govuk-link">Return to: Peer review application for underlying agreement variation</a>
  `,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = computed(() =>
    toOverallDecisionSummaryData(
      this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectDetermination)(),
      this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
      '../../file-download',
      false,
    ),
  );
}

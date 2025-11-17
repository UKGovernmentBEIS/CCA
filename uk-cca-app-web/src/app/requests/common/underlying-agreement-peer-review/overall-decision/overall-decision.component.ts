import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { toOverallDecisionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementPeerReviewQuery } from '../underlying-agreement-peer-review.selectors';

@Component({
  selector: 'cca-una-peer-review-overall-decision',
  template: `
    <div>
      <netz-page-heading>Overall decision</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>

    <a routerLink="../../" class="govuk-link">Return to: Peer review application for underlying agreement</a>
  `,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  private readonly store = inject(RequestTaskStore);

  protected readonly summaryData = computed(() =>
    toOverallDecisionSummaryData(
      this.store.select(underlyingAgreementPeerReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementPeerReviewQuery.selectReviewAttachments)(),
      '../../file-download',
      false,
    ),
  );
}

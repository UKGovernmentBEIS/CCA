import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { toBaselineAndTargetsSummaryDataWithDecision } from '../../underlying-agreement';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-target-period-6',
  template: `
    <div>
      <netz-page-heading>TP6 (2024)</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
    <a routerLink="../../" class="govuk-link">Return to: Peer review application for underlying agreement variation</a>
  `,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod6Component {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly summaryMetadata = {
    isTp5Period: false,
    baselineExists: null,
    downloadUrl: '../../file-download',
    isEditable: false,
    attachments: {
      submit: this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectUnderlyingAgreementVariationAttachments,
      )(),
      review: this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
    },
  };

  protected readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryDataWithDecision(
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
      )(),
      this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectTargetPeriod6)(),
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
      )(),
      this.summaryMetadata,
    ),
  );
}

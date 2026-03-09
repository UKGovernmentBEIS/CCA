import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { toBaselineAndTargetsSummaryDataWithDecision } from '../../underlying-agreement';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-target-period-5',
  template: `
    <div>
      <netz-page-heading>TP5 (2021-2022)</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod5Component {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly summaryMetadata = {
    isTp5Period: true,
    baselineExists: this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectTargetPeriod5)()
      ?.exist,
    downloadUrl: '../../file-download',
    isEditable: false,
    attachments: {
      submit: this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectUnderlyingAgreementVariationAttachments,
      )(),
      review: this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
    },
  };

  protected readonly summaryData = computed(() => {
    const targetPeriod5 = this.requestTaskStore.select(
      underlyingAgreementVariationPeerReviewQuery.selectTargetPeriod5,
    )();
    return toBaselineAndTargetsSummaryDataWithDecision(
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
      )(),
      targetPeriod5?.details,
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'),
      )(),
      this.summaryMetadata,
    );
  });
}

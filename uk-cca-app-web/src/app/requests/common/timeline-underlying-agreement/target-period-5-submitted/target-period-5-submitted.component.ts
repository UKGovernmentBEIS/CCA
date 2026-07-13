import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { toBaselineAndTargetsSummaryData } from '../../underlying-agreement/summaries';
import { underlyingAgreementRequestActionQuery } from '../timeline-underlying-agreement.selectors';

@Component({
  selector: 'cca-una-submitted-target-period-5',
  template: `
    <div>
      <netz-page-heading>TP5 (2021-2022)</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod5SubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryData({
      isTp5Period: true,
      baselineExists: this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod5Details)()
        .exist,
      sectorSchemeData: this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
      )(),
      targetPeriodDetails: this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectTargetPeriod5Details,
      )().details,
      attachments: this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      isEditable: false,
      multiFileDownloadUrl: '../../file-download',
    }),
  );
}

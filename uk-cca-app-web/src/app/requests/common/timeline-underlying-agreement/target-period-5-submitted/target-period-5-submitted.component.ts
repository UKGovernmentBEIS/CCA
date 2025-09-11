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
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod5SubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryData(
      true,
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod5Details)().exist,
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
      )(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod5Details)().details,
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      false,
      '../../file-download',
    ),
  );
}

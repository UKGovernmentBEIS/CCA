import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { toBaselineAndTargetsSummaryData } from '../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../timeline-underlying-agreement.selectors';

@Component({
  selector: 'cca-una-submitted-target-period-6',
  template: `
    <div>
      <netz-page-heading>TP6 (2024)</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod6SubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryData(
      false,
      true,
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
      )(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod6Details)(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      false,
      '../../file-download',
    ),
  );
}

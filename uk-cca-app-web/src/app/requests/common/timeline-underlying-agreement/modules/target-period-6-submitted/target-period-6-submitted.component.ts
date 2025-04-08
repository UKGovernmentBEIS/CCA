import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toBaselineAndTargetsSummaryData } from '../../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../../+state';

@Component({
  selector: 'cca-una-submitted-target-period-6',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './target-period-6-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod6SubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryData(
      false,
      true,
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectAccountReferenceDataSectorAssociationDetails,
      )(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod6Details)(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      false,
      '../../file-download',
    ),
  );
}

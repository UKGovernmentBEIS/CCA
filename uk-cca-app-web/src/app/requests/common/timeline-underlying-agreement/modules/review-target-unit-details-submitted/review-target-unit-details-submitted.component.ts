import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toReviewTargetUnitDetailsUNAReviewSummaryData } from '../../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../../+state';

@Component({
  selector: 'cca-una-submitted-target-unit-details',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './review-target-unit-details-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toReviewTargetUnitDetailsUNAReviewSummaryData(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectUnderlyingAgreementTargetUnitDetails,
      )(),
      false,
    ),
  );
}

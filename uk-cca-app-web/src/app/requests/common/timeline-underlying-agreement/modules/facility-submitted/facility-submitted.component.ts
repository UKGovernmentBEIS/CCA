import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toFacilitySummaryData } from '../../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../../+state';

@Component({
  selector: 'cca-una-submitted-facility',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './facility-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilitySubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.requestActionStore.select(
    underlyingAgreementRequestActionQuery.selectFacility(this.facilityId),
  )();

  protected readonly summaryData = computed(() =>
    toFacilitySummaryData(
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectFacility(this.facilityId))(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      false,
      '../../../file-download',
    ),
  );
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { underlyingAgreementRequestActionQuery } from '@requests/common';
import { SplitByProductTableComponent } from '@requests/common';

@Component({
  selector: 'cca-products-table',
  templateUrl: './summary-products-timeline.component.html',
  imports: [PageHeadingComponent, SplitByProductTableComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryProductsTimelineComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly underlyingAgreement = this.requestActionStore.select(
    underlyingAgreementRequestActionQuery.selectUnderlyingAgreement,
  )();

  private readonly facilityIndex =
    this.underlyingAgreement?.facilities?.findIndex((facility) => facility.facilityId === this.facilityId) ?? -1;

  protected readonly facility = computed(() =>
    this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectFacility(this.facilityId))(),
  );

  protected readonly products = computed(() => {
    if (this.facilityIndex === -1) return [];

    return (
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectFacilityBaselineEnergyConsumption(this.facilityIndex),
      )()?.variableEnergyConsumptionDataByProduct ?? []
    );
  });

  protected readonly facilityThroughputUnit = computed(() => {
    if (this.facilityIndex === -1) return undefined;

    return this.requestActionStore.select(
      underlyingAgreementRequestActionQuery.selectFacilityTargetComposition(this.facilityIndex),
    )()?.measurementType;
  });
}

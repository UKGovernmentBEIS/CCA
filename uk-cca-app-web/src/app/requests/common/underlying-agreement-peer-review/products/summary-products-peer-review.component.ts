import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementPeerReviewQuery } from '@requests/common';
import { SplitByProductTableComponent } from '@requests/common';

@Component({
  selector: 'cca-products-table',
  templateUrl: './summary-products-peer-review.component.html',
  imports: [PageHeadingComponent, SplitByProductTableComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryProductsPeerReviewComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly underlyingAgreement = this.requestTaskStore.select(
    underlyingAgreementPeerReviewQuery.selectUnderlyingAgreement,
  )();

  private readonly facilityIndex = this.underlyingAgreement?.facilities?.findIndex(
    (facility) => facility.facilityId === this.facilityId,
  );

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementPeerReviewQuery.selectFacility(this.facilityId))(),
  );

  protected readonly products = computed(() => {
    if (this.facilityIndex === -1) return [];

    return (
      this.requestTaskStore.select(
        underlyingAgreementPeerReviewQuery.selectFacilityBaselineEnergyConsumption(this.facilityIndex),
      )()?.variableEnergyConsumptionDataByProduct ?? []
    );
  });

  protected readonly facilityThroughputUnit = computed(() => {
    if (this.facilityIndex === -1) return undefined;

    return this.requestTaskStore.select(
      underlyingAgreementPeerReviewQuery.selectFacilityTargetComposition(this.facilityIndex),
    )()?.measurementType;
  });
}

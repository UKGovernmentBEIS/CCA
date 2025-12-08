import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { FacilityWizardStep, underlyingAgreementQuery } from '@requests/common';

import { SplitByProductTableComponent } from '../../../split-by-product-table';

@Component({
  selector: 'cca-products-table',
  templateUrl: './summary-products.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, SplitByProductTableComponent, RouterLink],
})
export class SummaryProductsComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly underlyingAgreement = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreement,
  );

  private readonly facilityIndex = this.underlyingAgreement()?.facilities?.findIndex(
    (facility) => facility.facilityId === this.facilityId,
  );

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  protected readonly products = computed(
    () =>
      this.requestTaskStore.select(
        underlyingAgreementQuery.selectFacilityBaselineEnergyConsumption(this.facilityIndex),
      )()?.variableEnergyConsumptionDataByProduct ?? [],
  );

  protected readonly facilityThroughputUnit = computed(() => {
    if (this.facilityIndex === -1) return;

    return this.requestTaskStore.select(underlyingAgreementQuery.selectFacilityTargetComposition(this.facilityIndex))()
      ?.measurementType;
  });

  protected readonly canChangeProducts = computed(
    () =>
      this.authStore.select(selectUserId)() === this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)(),
  );

  protected readonly FacilityWizardStep = FacilityWizardStep;
}

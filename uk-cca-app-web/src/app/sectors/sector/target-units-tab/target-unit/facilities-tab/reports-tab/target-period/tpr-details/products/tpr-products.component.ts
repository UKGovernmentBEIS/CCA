import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { SplitByProductTableComponent } from '@requests/common';

import { FacilityTargetPeriodReportStore } from '../../../../facility-target-period-report.store';

@Component({
  selector: 'cca-tpr-products',
  template: `
    <netz-page-heading>Products</netz-page-heading>

    <cca-split-by-product-table
      [products]="products()"
      [facilityThroughputUnit]="facilityThroughputUnit()"
      [readOnly]="true"
    />
  `,
  imports: [PageHeadingComponent, SplitByProductTableComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprProductsComponent {
  private readonly facilityTargetPeriodReportStore = inject(FacilityTargetPeriodReportStore);

  private readonly state = this.facilityTargetPeriodReportStore.stateAsSignal;

  private readonly baselineAndTargets = computed(() => this.state().details.baselineAndTargets);
  protected readonly products = computed(() => this.baselineAndTargets().variableEnergyConsumptionDataByProduct ?? []);
  protected readonly facilityThroughputUnit = computed(() => this.baselineAndTargets().measurementType);
}

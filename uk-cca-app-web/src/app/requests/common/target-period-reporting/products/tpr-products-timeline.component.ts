import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SplitByProductTableComponent, tprFormActionQuery } from '@requests/common';

@Component({
  selector: 'cca-tpr-products-timeline',
  template: `
    <netz-page-heading>Split by product</netz-page-heading>

    <cca-split-by-product-table
      [products]="products()"
      [facilityThroughputUnit]="facilityThroughputUnit()"
      [readOnly]="true"
    />

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SplitByProductTableComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprProductsTimelineComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly products = computed(
    () =>
      this.requestActionStore.select(tprFormActionQuery.selectPerformanceData)()?.baselineAndTargets
        ?.variableEnergyConsumptionDataByProduct ?? [],
  );

  protected readonly facilityThroughputUnit = computed(
    () =>
      this.requestActionStore.select(tprFormActionQuery.selectPerformanceData)()?.baselineAndTargets?.measurementType,
  );
}

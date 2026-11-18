import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SplitByProductTableComponent, tprFormQuery } from '@requests/common';

@Component({
  selector: 'cca-tpr-products',
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
export class TprProductsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly products = computed(
    () =>
      this.requestTaskStore.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets
        ?.variableEnergyConsumptionDataByProduct ?? [],
  );

  protected readonly facilityThroughputUnit = computed(
    () => this.requestTaskStore.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets?.measurementType,
  );
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestTaskStore } from '@netz/common/store';
import { decideVariableEnergyType } from '@requests/common';

import { tprFormQuery } from '../../../target-period-reporting-form.selectors';
import { TprThroughputSplitByProductComponent } from './split-by-product/tpr-throughput-split-by-product.component';
import { TprThroughputTotalsOnlyComponent } from './totals-only/tpr-throughput-totals-only.component';

@Component({
  selector: 'cca-tpr-throughput-details',
  template: `
    @if (variableEnergyType() === 'BY_PRODUCT') {
      <cca-tpr-throughput-split-by-product />
    } @else {
      <cca-tpr-throughput-totals-only />
    }
  `,
  imports: [TprThroughputSplitByProductComponent, TprThroughputTotalsOnlyComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprThroughputDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly referenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);

  protected readonly variableEnergyType = computed(() =>
    decideVariableEnergyType(this.referenceData()?.baselineAndTargets?.variableEnergyType),
  );
}

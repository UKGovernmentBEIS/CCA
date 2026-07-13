import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { resolveProductEnergyCarbonIntensity, tprFormActionQuery } from '@requests/common';
import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementTypeToUnitPipe } from '@shared/pipes';
import { toNumber } from '@shared/utils';

@Component({
  selector: 'cca-tpr-throughput-submitted',
  templateUrl: './tpr-throughput-submitted.component.html',
  imports: [DecimalPipe, PageHeadingComponent, TableComponent, MeasurementTypeToUnitPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprThroughputSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly performanceData = this.requestActionStore.select(tprFormActionQuery.selectPerformanceData);
  private readonly details = this.requestActionStore.select(tprFormActionQuery.selectDetails);
  protected readonly reportType = computed(() => this.details()?.reportType);
  protected readonly measurementType = computed(() => this.performanceData()?.baselineAndTargets?.measurementType);
  protected readonly throughputUnit = computed(() => this.performanceData()?.baselineAndTargets?.throughputUnit);

  protected readonly variableEnergyType = computed(
    () => this.performanceData()?.baselineAndTargets?.variableEnergyType,
  );

  protected readonly tableColumns = computed<GovukTableColumn[]>(() => [
    { field: 'productName', header: 'Product name' },
    { field: 'baselineYear', header: 'Baseline year' },
    { field: 'baselineEnergyIntensity', header: 'Baseline energy intensity' },
    {
      field: 'targetImprovement',
      header: this.reportType() === 'INTERIM' ? 'Interim target %' : 'Improvement target %',
    },
    { field: 'actualThroughput', header: 'Actual throughput' },
    { field: 'adjustedThroughput', header: 'Adjusted throughput' },
    { field: 'targetEnergy', header: 'Target energy' },
  ]);

  protected readonly tableRows = computed(() => {
    const baselineProducts = this.performanceData()?.baselineAndTargets?.variableEnergyConsumptionDataByProduct ?? [];
    const savedProducts = this.performanceData()?.throughputDetails?.variableEnergyConsumptionDataByProduct ?? [];
    const savedByName = new Map(savedProducts.map((product) => [product.productName, product]));

    return baselineProducts
      .filter((product) => product.productStatus !== 'EXCLUDED')
      .map((product) => {
        const persisted = savedByName.get(product.productName);
        const intensity = resolveProductEnergyCarbonIntensity(product);

        return {
          productName: product.productName,
          baselineYear: product.baselineYear,
          baselineEnergyIntensity: intensity,
          throughputUnit: product.throughputUnit,
          targetImprovement: toNumber(persisted?.targetImprovement),
          actualThroughput: toNumber(persisted?.actualThroughput),
          adjustedThroughput: toNumber(persisted?.adjustedThroughput),
          targetEnergy: toNumber(persisted?.targetEnergy),
        };
      });
  });

  protected readonly totalsActualThroughput = computed(() =>
    toNumber(this.performanceData()?.throughputDetails?.actualThroughput),
  );

  protected readonly totalsTargetVariableEnergy = computed(() =>
    toNumber(this.performanceData()?.throughputDetails?.totalTargetVariableEnergy),
  );

  protected readonly measurementUnitLabel = computed(() => MEASUREMENT_TYPE_TO_UNIT_MAP[this.measurementType()]);
}

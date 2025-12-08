import { DecimalPipe, TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukTableColumn, TableComponent, TagComponent } from '@netz/govuk-components';

import { ProductVariableEnergyConsumptionData } from 'cca-api';

import { ProductStatusColorPipe } from '../pipes';
import { MeasurementTypeToUnitPipe } from '../pipes';

@Component({
  selector: 'cca-split-by-product-table',
  templateUrl: './split-by-product-table.component.html',
  imports: [
    TableComponent,
    TagComponent,
    TitleCasePipe,
    ProductStatusColorPipe,
    MeasurementTypeToUnitPipe,
    RouterLink,
    DecimalPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SplitByProductTableComponent {
  private readonly authStore = inject(AuthStore);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly products = input<ProductVariableEnergyConsumptionData[]>([]);

  private readonly decimalPipe = new DecimalPipe('en-GB');

  protected readonly facilityThroughputUnit = input<
    'ENERGY_KWH' | 'ENERGY_MWH' | 'ENERGY_GJ' | 'CARBON_KG' | 'CARBON_TONNE'
  >();

  protected readonly readOnly = input(false);

  private readonly baseColumns: GovukTableColumn[] = [
    { field: 'productName', header: 'Product name' },
    { field: 'productStatus', header: 'Status' },
    { field: 'baselineYear', header: 'Baseline year' },
    { field: 'energy', header: 'Baseline variable energy' },
    { field: 'throughput', header: 'Baseline throughput' },
    { field: 'energyIntensity', header: 'Energy intensity' },
    { field: 'actions', header: 'Actions' },
  ];

  protected readonly tableColumns = computed(() =>
    this.readOnly() ? this.baseColumns.filter((column) => column.field !== 'actions') : this.baseColumns,
  );

  protected readonly canAlterProducts = computed(
    () =>
      this.authStore.select(selectUserId)() === this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)(),
  );

  protected calculateEnergyIntensity(product: ProductVariableEnergyConsumptionData): string {
    const throughput = Number(product.throughput ?? 0);
    if (throughput === 0) return this.decimalPipe.transform(0, '1.0-7') ?? '0';
    const intensity = Number(product.energy ?? 0) / throughput;
    return this.decimalPipe.transform(intensity, '1.0-7') ?? '0';
  }
}

import { DecimalPipe, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input, Signal, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  GovukTableColumn,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TableComponent,
} from '@netz/govuk-components';
import {
  calculateAdjustedImprovementTargetForProduct,
  calculateAdjustedThroughput,
  calculateFacilityImprovementTarget,
  calculateTargetEnergyForProduct,
  calculateThroughputAdjustmentFactor,
  MeasurementTypeToUnitPipe,
  toNumber,
} from '@requests/common';
import { PaginationComponent } from '@shared/components';

import { PerformanceDataFacilityInputData, PerformanceDataFacilityReferenceData } from 'cca-api';

@Component({
  selector: 'cca-throughput-details-summary',
  templateUrl: './throughput-details-summary.component.html',
  imports: [
    DecimalPipe,
    PercentPipe,
    RouterLink,
    TableComponent,
    MeasurementTypeToUnitPipe,
    PaginationComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThroughputDetailsSummaryComponent {
  protected readonly referenceData = input<PerformanceDataFacilityReferenceData>();
  protected readonly performanceData = input<PerformanceDataFacilityInputData>();
  protected readonly isEditable = input<boolean>(true);
  protected readonly detailsLink = input<string>('../details');
  protected readonly reportType = input<'INTERIM' | 'FINAL'>('FINAL');
  protected readonly targetPeriodType = input<'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9'>('TP5');

  protected readonly currentPage = signal(1);
  protected readonly pageSize = signal(10);

  protected readonly measurementUnit = computed(() => this.referenceData()?.baselineAndTargets?.measurementType);

  protected readonly tableColumns: Signal<GovukTableColumn[]> = signal([
    { field: 'productName', header: 'Product name' },
    { field: 'baselineYear', header: 'Baseline year' },
    { field: 'energy', header: 'Baseline energy intensity' },
    { field: 'improvementTarget', header: 'Improvement target %' },
    { field: 'throughput', header: 'Actual throughput' },
    { field: 'adjustedThroughput', header: 'Adjusted throughput' },
    { field: 'targetEnergy', header: 'Target energy' },
  ]);

  protected readonly tableRows = computed(() => {
    const baselineProducts = this.referenceData()?.baselineAndTargets?.variableEnergyConsumptionDataByProduct ?? [];
    const savedProducts = this.performanceData()?.throughputDetails?.variableEnergyConsumptionDataByProduct ?? [];
    const referenceData = this.referenceData();
    const savedProductsByName = new Map(savedProducts.map((product) => [product.productName, product]));

    const baselineDateStr = referenceData?.baselineAndTargets?.baselineDate;
    const parsedBaseYear = baselineDateStr ? Number.parseInt(baselineDateStr.substring(0, 4), 10) : null;
    const facilityBaseYear = parsedBaseYear != null && !Number.isNaN(parsedBaseYear) ? parsedBaseYear : null;

    const facilityImprovementTarget = calculateFacilityImprovementTarget(
      referenceData,
      this.reportType(),
      this.targetPeriodType(),
    );

    const throughputAdjustmentFactor = calculateThroughputAdjustmentFactor(
      toNumber(this.performanceData()?.energyFuelDetails?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy),
      toNumber(this.performanceData()?.energyFuelDetails?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy),
      toNumber(this.performanceData()?.energyFuelDetails?.electricitySuppliedFromCHP),
    );

    const useSRM = referenceData?.baselineAndTargets?.usedReportingMechanism ?? false;

    return baselineProducts
      .filter((product) => savedProductsByName.get(product.productName)?.actualThroughput != null)
      .map((product) => {
        const savedProduct = savedProductsByName.get(product.productName);
        const actualThroughput = savedProduct?.actualThroughput ?? null;

        const productBaseYear = product.baselineYear;
        let improvementTarget = facilityImprovementTarget;

        if (facilityBaseYear && productBaseYear !== facilityBaseYear) {
          improvementTarget = calculateAdjustedImprovementTargetForProduct(
            referenceData,
            this.reportType(),
            this.targetPeriodType(),
            facilityBaseYear,
            productBaseYear,
          );
        }

        const adjustedThroughput =
          calculateAdjustedThroughput(actualThroughput, throughputAdjustmentFactor, useSRM) ?? 0;
        const targetEnergy = calculateTargetEnergyForProduct(product.energy, adjustedThroughput, improvementTarget);

        return {
          productName: product.productName,
          baselineYear: product.baselineYear,
          energy: product.energy,
          throughputUnit: product.throughputUnit,
          improvementTarget,
          throughput: actualThroughput,
          adjustedThroughput,
          targetEnergy,
        };
      });
  });

  protected readonly totalTargetVariableEnergy = computed(() =>
    this.tableRows().reduce((sum, row) => sum + (row.targetEnergy ?? 0), 0),
  );

  protected readonly shouldShowPagination = computed(() => this.tableRows().length > 10);

  protected readonly paginatedTableRows = computed(() => {
    const all = this.tableRows();
    const start = (this.currentPage() - 1) * this.pageSize();
    const end = start + this.pageSize();
    return all.slice(start, end);
  });

  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  onPageSizeChange(pageSize: number): void {
    this.currentPage.set(1);
    this.pageSize.set(pageSize);
  }
}

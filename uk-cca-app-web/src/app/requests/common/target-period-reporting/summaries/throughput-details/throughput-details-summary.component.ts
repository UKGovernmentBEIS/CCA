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
  calculateAdjustedImprovementTarget,
  calculateAdjustedThroughput,
  calculateFacilityImprovementTarget,
  calculateProductTargetEnergy,
  calculateThroughputAdjustmentFactor,
  isCarbonMeasurementType,
  resolveProductEnergyCarbonIntensity,
} from '@requests/common';
import { PaginationComponent } from '@shared/components';
import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementTypeToUnitPipe } from '@shared/pipes';
import { to7DecimalPlacesNumber } from '@shared/utils';
import BigNumber from 'bignumber.js';

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

  protected readonly isCarbonMeasurement = computed(() =>
    isCarbonMeasurementType(MEASUREMENT_TYPE_TO_UNIT_MAP[this.measurementUnit()]),
  );

  protected readonly tableColumns: Signal<GovukTableColumn[]> = computed(() => [
    { field: 'productName', header: 'Product name' },
    { field: 'baselineYear', header: 'Baseline year' },
    {
      field: 'energy',
      header: this.isCarbonMeasurement() ? 'Baseline CO2e intensity' : 'Baseline energy intensity',
    },
    {
      field: 'improvementTarget',
      header: this.reportType() === 'INTERIM' ? 'Interim target %' : 'Improvement target %',
    },
    { field: 'throughput', header: 'Actual throughput' },
    { field: 'adjustedThroughput', header: 'Adjusted throughput' },
    {
      field: 'targetEnergy',
      header: this.isCarbonMeasurement() ? 'Target CO2e' : 'Target energy',
    },
  ]);

  protected readonly tableRows = computed(() => {
    const baselineProducts = this.referenceData()?.baselineAndTargets?.variableEnergyConsumptionDataByProduct ?? [];
    const savedProducts = this.performanceData()?.throughputDetails?.variableEnergyConsumptionDataByProduct ?? [];
    const referenceData = this.referenceData();
    const savedProductsByName = new Map(savedProducts.map((product) => [product.productName, product]));

    const facilityBaselineYear = referenceData?.baselineAndTargets?.baselineYear;

    const facilityImprovementTarget = calculateFacilityImprovementTarget(
      referenceData,
      this.reportType(),
      this.targetPeriodType(),
    );

    const throughputAdjustmentFactor = calculateThroughputAdjustmentFactor(
      this.performanceData()?.energyFuelDetails?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy ?? '0',
      this.performanceData()?.energyFuelDetails?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy ?? '0',
      this.performanceData()?.energyFuelDetails?.electricitySuppliedFromCHP ?? '0',
    );

    const useSRM = referenceData?.baselineAndTargets?.usedReportingMechanism ?? false;

    return baselineProducts
      .filter((product) => savedProductsByName.get(product.productName)?.actualThroughput != null)
      .map((product) => {
        const savedProduct = savedProductsByName.get(product.productName);
        const actualThroughput = savedProduct?.actualThroughput ?? null;

        const productBaseYear = product.baselineYear;
        let improvementTarget = facilityImprovementTarget;

        if (facilityBaselineYear != null && productBaseYear !== facilityBaselineYear) {
          improvementTarget = calculateAdjustedImprovementTarget(
            referenceData,
            this.reportType(),
            this.targetPeriodType(),
            facilityBaselineYear,
            productBaseYear,
          );
        }

        const adjustedThroughput =
          calculateAdjustedThroughput(actualThroughput, throughputAdjustmentFactor, useSRM) ?? new BigNumber(0);

        const baselineEnergyIntensity = resolveProductEnergyCarbonIntensity(product);

        const targetEnergy = calculateProductTargetEnergy(
          baselineEnergyIntensity,
          adjustedThroughput,
          improvementTarget,
        );

        return {
          productName: product.productName,
          baselineYear: product.baselineYear,
          energy: baselineEnergyIntensity,
          throughputUnit: product.throughputUnit,
          improvementTarget,
          throughput: actualThroughput,
          adjustedThroughput,
          targetEnergy,
        };
      });
  });

  readonly totalTargetVariableEnergy = computed(() =>
    this.tableRows().reduce((sum, row) => sum.plus(row.targetEnergy ?? new BigNumber(0)), new BigNumber(0)),
  );

  readonly displayRounded = to7DecimalPlacesNumber;

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

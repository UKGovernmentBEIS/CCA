import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  GovukTableColumn,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TableComponent,
} from '@netz/govuk-components';
import { boolToString, primaryCarbonDisplayUnit, tprFormActionQuery } from '@requests/common';

import {
  buildSubmittedEnergyFuelRows,
  calculateSubmittedThroughputAdjustmentFactor,
  resolveCo2FactorUnit,
  resolveMeasurementTypeUnit,
} from './energy-fuel-amount-submitted.utils';

@Component({
  selector: 'cca-energy-fuel-amount-submitted',
  templateUrl: './energy-fuel-amount-submitted.component.html',
  imports: [
    DecimalPipe,
    PageHeadingComponent,
    TableComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnergyFuelAmountSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly performanceData = this.requestActionStore.select(tprFormActionQuery.selectPerformanceData);

  protected readonly measurementUnit = computed(() =>
    resolveMeasurementTypeUnit(this.performanceData()?.baselineAndTargets?.measurementType),
  );

  protected readonly energyFuelDetails = computed(() => this.performanceData()?.energyFuelDetails);
  protected readonly resolvedMeasurementUnit = computed(() => resolveCo2FactorUnit(this.measurementUnit()));

  protected readonly tableColumns = computed<GovukTableColumn[]>(() => [
    { field: 'fuelType', header: 'Fuel type' },
    {
      field: 'co2ConversionFactor',
      header: `CO2 conversion factor (kgCO2e/${this.resolvedMeasurementUnit()})`,
    },
    { field: 'deliveredEnergy', header: 'Delivered energy (excluding UK ETS)' },
    { field: 'primaryEnergyConversionFactor', header: 'Primary energy conversion factor' },
    {
      field: 'primaryEnergy',
      header:
        this.resolvedMeasurementUnit() === 'kWh' && this.measurementUnit() !== 'kWh'
          ? `Primary CO2e (${primaryCarbonDisplayUnit(this.measurementUnit())})`
          : `Primary energy (${this.measurementUnit()})`,
    },
  ]);

  protected readonly tableRows = computed(() =>
    buildSubmittedEnergyFuelRows(this.energyFuelDetails(), this.measurementUnit()),
  );

  protected readonly usedReportingMechanism = computed(
    () => this.performanceData()?.baselineAndTargets?.usedReportingMechanism,
  );

  protected readonly isAtLeast70PercentEnergyUsedText = computed(() =>
    boolToString(this.energyFuelDetails()?.atLeastSeventyPercentEnergyUsed),
  );

  protected readonly electricitySuppliedFromCHP = computed(() => this.energyFuelDetails()?.electricitySuppliedFromCHP);

  protected readonly throughputAdjustmentFactor = computed(() =>
    calculateSubmittedThroughputAdjustmentFactor(this.energyFuelDetails()),
  );
}

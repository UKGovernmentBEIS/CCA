import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  GovukTableColumn,
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TableComponent,
} from '@netz/govuk-components';

import { PerformanceDataFacilityInputEnergyFuelDetails } from 'cca-api';

import { boolToString } from '../../../utils';
import { EnergyFuelRow } from '../../target-period-reporting-form.types';
import { buildEnergyFuelRows, calculateThroughputAdjustmentFactor, toNumber } from '../../utils';

@Component({
  selector: 'cca-energy-fuel-amount-summary',
  templateUrl: './energy-fuel-amount-summary.component.html',
  imports: [
    DecimalPipe,
    RouterLink,
    TableComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnergyFuelAmountSummaryComponent {
  readonly energyFuelDetails = input<PerformanceDataFacilityInputEnergyFuelDetails>();
  readonly isEditable = input<boolean>(true);
  readonly measurementType = input<string>('kWh');
  readonly detailsLink = input<string>('../details');
  readonly usedReportingMechanism = input<boolean>(false);

  private readonly isCarbonMeasurement = computed(() => ['kg', 'tonne'].includes(this.measurementType()));
  protected readonly measurementUnit = computed(() => (this.isCarbonMeasurement() ? 'kWh' : this.measurementType()));

  protected readonly tableColumns = computed<GovukTableColumn[]>(() => {
    const columns: GovukTableColumn[] = [
      { field: 'fuelType', header: 'Fuel type' },
      {
        field: 'co2ConversionFactor',
        header: `CO2 conversion factor (kgCO2e/${this.measurementUnit()})`,
      },
      { field: 'deliveredEnergy', header: 'Delivered energy (excluding UK ETS)' },
      { field: 'primaryEnergyConversionFactor', header: 'Primary energy conversion factor' },
      {
        field: 'primaryEnergy',
        header: this.isCarbonMeasurement() ? 'Primary carbon (kgCO2e)' : `Primary energy (${this.measurementType()})`,
      },
    ];

    if (this.isEditable()) {
      columns.push({ field: 'actions', header: 'Actions' });
    }

    return columns;
  });

  protected readonly tableRows = computed<EnergyFuelRow[]>(() =>
    buildEnergyFuelRows(this.energyFuelDetails(), this.measurementType()),
  );

  protected readonly isAtLeast70PercentEnergyUsed = computed(
    () => this.energyFuelDetails()?.atLeastSeventyPercentEnergyUsed,
  );

  protected readonly throughputAdjustmentFactor = computed(() => {
    const details = this.energyFuelDetails();
    const gridElectricity = toNumber(details?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy);
    const nonGridElectricity = toNumber(details?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy);
    const chpElectricity = toNumber(details?.electricitySuppliedFromCHP);

    return calculateThroughputAdjustmentFactor(gridElectricity, nonGridElectricity, chpElectricity);
  });

  protected readonly isAtLeast70PercentEnergyUsedText = computed(
    () => boolToString(this.isAtLeast70PercentEnergyUsed()) ?? 'Not provided',
  );

  protected readonly electricitySuppliedFromCHP = computed(() =>
    toNumber(this.energyFuelDetails()?.electricitySuppliedFromCHP),
  );
}

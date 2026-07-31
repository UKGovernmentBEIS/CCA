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
import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementUnit } from '@shared/pipes';
import { to7DecimalPlacesNumber, toNumber } from '@shared/utils';

import { PerformanceDataFacilityInputEnergyFuelDetails } from 'cca-api';

import { boolToString } from '../../../utils';
import { EnergyFuelRow } from '../../target-period-reporting-form.types';
import {
  buildEnergyFuelRows,
  calculateThroughputAdjustmentFactor,
  isCarbonMeasurementType,
  primaryCarbonDisplayUnit,
} from '../../utils';

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
  readonly measurementUnit = input<MeasurementUnit>(MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH);
  readonly detailsLink = input<string>('../details');
  readonly usedReportingMechanism = input<boolean>(false);

  private readonly isCarbonMeasurement = computed(() => isCarbonMeasurementType(this.measurementUnit()));

  protected readonly resolvedMeasurementUnit = computed<MeasurementUnit>(() =>
    this.isCarbonMeasurement() ? MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH : this.measurementUnit(),
  );

  protected readonly tableColumns = computed<GovukTableColumn[]>(() => {
    const columns: GovukTableColumn[] = [
      { field: 'fuelType', header: 'Fuel type' },
      {
        field: 'co2ConversionFactor',
        header: `CO2 conversion factor (kgCO2e/${this.resolvedMeasurementUnit()})`,
      },
      { field: 'deliveredEnergy', header: 'Delivered energy (excluding UK ETS)' },
      { field: 'primaryEnergyConversionFactor', header: 'Primary energy conversion factor' },
      {
        field: 'primaryEnergy',
        header: this.isCarbonMeasurement()
          ? `Primary CO2e (${primaryCarbonDisplayUnit(this.measurementUnit())})`
          : `Primary energy (${this.measurementUnit()})`,
      },
    ];

    if (this.isEditable()) {
      columns.push({ field: 'actions', header: 'Actions' });
    }

    return columns;
  });

  protected readonly tableRows = computed<EnergyFuelRow[]>(() =>
    buildEnergyFuelRows(this.energyFuelDetails(), this.measurementUnit()),
  );

  protected readonly isAtLeast70PercentEnergyUsed = computed(
    () => this.energyFuelDetails()?.atLeastSeventyPercentEnergyUsed,
  );

  readonly throughputAdjustmentFactor = computed(() => {
    const details = this.energyFuelDetails();
    return calculateThroughputAdjustmentFactor(
      details?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy ?? '0',
      details?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy ?? '0',
      details?.electricitySuppliedFromCHP ?? '0',
    );
  });

  protected readonly isAtLeast70PercentEnergyUsedText = computed(
    () => boolToString(this.isAtLeast70PercentEnergyUsed()) ?? 'Not provided',
  );

  readonly displayRounded = to7DecimalPlacesNumber;

  protected readonly electricitySuppliedFromCHP = computed(() =>
    toNumber(this.energyFuelDetails()?.electricitySuppliedFromCHP),
  );
}

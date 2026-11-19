import { DecimalPipe } from '@angular/common';
import { Component, computed, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import {
  GovukTableColumn,
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TableComponent,
} from '@netz/govuk-components';

import { EmissionsTradingSchemeDataInputDetails } from 'cca-api';

import { EtsMeasurementType } from '../ets-reporting.types';
import { requiresEtsFuelDetails } from '../ets-reporting.validation';
import {
  buildEtsFuelSummaryRows,
  createEtsFuelDetails,
  getEtsDeliveredUnit,
  getEtsMeasurementUnit,
  isEtsCarbonMeasurement,
} from '../ets-reporting-calculations';

@Component({
  selector: 'cca-ets-energy-fuel-summary',
  templateUrl: './ets-energy-fuel-summary.component.html',
  styles: `
    .fuel-actions {
      white-space: nowrap;
    }
    .fuel-name {
      overflow-wrap: anywhere;
    }
  `,
  imports: [
    DecimalPipe,
    RouterLink,
    PendingButtonDirective,
    TableComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
  ],
})
export class EtsEnergyFuelSummaryComponent {
  readonly inputDetails = input<EmissionsTradingSchemeDataInputDetails>();
  readonly measurementType = input.required<EtsMeasurementType>();
  readonly isEditable = input(false);
  readonly removeFuel = output<number>();
  readonly questionsLink = input('../ultra-small-emitter');
  readonly detailsLink = input('../details');

  protected readonly reportsFuels = computed(() => requiresEtsFuelDetails(this.inputDetails()));
  protected readonly energyFuelDetails = computed(() => this.inputDetails()?.energyFuelDetails);
  protected readonly measurementUnit = computed(() => getEtsMeasurementUnit(this.measurementType()));
  protected readonly isCarbon = computed(() => isEtsCarbonMeasurement(this.measurementType()));
  protected readonly primaryLabel = computed(() => (this.isCarbon() ? 'Primary CO2e' : 'Primary energy'));
  protected readonly tableRows = computed(() =>
    buildEtsFuelSummaryRows(this.measurementType(), this.energyFuelDetails()),
  );
  protected readonly totals = computed(() => createEtsFuelDetails(this.measurementType(), this.tableRows()));
  protected readonly tableColumns = computed<GovukTableColumn[]>(() => [
    { field: 'fuelType', header: 'Energy/fuel type', isHeader: true, widthClass: 'govuk-!-width-one-quarter' },
    {
      field: 'co2ConversionFactor',
      header: `CO2 conversion factor (kgCO2e/${getEtsDeliveredUnit(this.measurementType())})`,
      isNumeric: true,
    },
    {
      field: 'deliveredEnergy',
      header: 'Delivered energy',
      isNumeric: true,
    },
    {
      field: 'primaryEnergyConversionFactor',
      header: 'Primary energy conversion factor',
      isNumeric: true,
    },
    {
      field: 'primaryEnergy',
      header: `${this.primaryLabel()} (${this.measurementUnit()})`,
      isNumeric: true,
    },
    ...(this.isEditable() ? [{ field: 'actions', header: 'Actions', isNumeric: true }] : []),
  ]);

  protected answer(value: boolean | undefined): string {
    return value === true ? 'Yes' : value === false ? 'No' : 'Not provided';
  }
}

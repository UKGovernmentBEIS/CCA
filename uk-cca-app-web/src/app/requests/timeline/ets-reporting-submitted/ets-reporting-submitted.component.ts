import { DecimalPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { FUEL_MAP } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { toNumber } from '@shared/utils';

import {
  getEtsDeliveredUnit,
  getEtsFuelLabel,
  getEtsPrimaryUnit,
  isEtsCarbonMeasurement,
} from '../../tasks/ets-reporting/ets-reporting-calculations';
import { etsReportingActionQuery } from './ets-reporting-submitted.selectors';
import { toEtsReportingSubmittedSummary } from './ets-reporting-submitted-summary-data';

const FUEL_ORDER: Record<string, number> = Object.fromEntries(Object.keys(FUEL_MAP).map((key, index) => [key, index]));

@Component({
  selector: 'cca-ets-reporting-submitted',
  templateUrl: './ets-reporting-submitted.component.html',
  styles: `
    .fuel-name {
      overflow-wrap: anywhere;
    }
  `,
  imports: [DecimalPipe, SummaryComponent, TableComponent],
})
export class EtsReportingSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly payload = this.requestActionStore.select(etsReportingActionQuery.selectPayload);
  private readonly performanceData = this.requestActionStore.select(etsReportingActionQuery.selectPerformanceData);

  protected readonly calculatedResults = this.requestActionStore.select(
    etsReportingActionQuery.selectCalculatedResults,
  );
  protected readonly summaryData = computed(() => toEtsReportingSubmittedSummary(this.payload()));
  protected readonly reportsFuels = computed(
    () => this.performanceData()?.isUltraSmallEmitter === false || this.calculatedResults() != null,
  );
  protected readonly isCarbon = computed(() => isEtsCarbonMeasurement(this.performanceData()?.measurementType));
  protected readonly measurementUnit = computed(() => getEtsPrimaryUnit(this.performanceData()?.measurementType));
  protected readonly tableColumns = computed<GovukTableColumn[]>(() => [
    { field: 'name', header: 'Energy/fuel type', isHeader: true, widthClass: 'govuk-!-width-one-quarter' },
    {
      field: 'conversionFactor',
      header: `CO2 conversion factor (kgCO2e/${getEtsDeliveredUnit(this.performanceData()?.measurementType)})`,
      isNumeric: true,
    },
    { field: 'deliveredEnergy', header: 'Delivered energy', isNumeric: true },
    { field: 'primaryConversionFactor', header: 'Primary energy conversion factor', isNumeric: true },
    {
      field: 'primaryEnergy',
      header: `${this.isCarbon() ? 'Primary CO2e' : 'Primary energy'} (${this.measurementUnit()})`,
      isNumeric: true,
    },
  ]);
  protected readonly tableRows = computed(() =>
    (this.performanceData()?.fuels ?? [])
      .filter((fuel) => toNumber(fuel.deliveredEnergy) !== 0)
      .sort((a, b) => {
        const orderA = FUEL_ORDER[a.fixedConversionFactorCode] ?? Infinity;
        const orderB = FUEL_ORDER[b.fixedConversionFactorCode] ?? Infinity;
        return orderA - orderB;
      })
      .map((fuel) => ({ ...fuel, name: getEtsFuelLabel(fuel.fixedConversionFactorCode, fuel.name) })),
  );
}

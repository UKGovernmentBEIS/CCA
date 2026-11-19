import { SummaryData, SummaryFactory } from '@shared/components';
import { getFacilityPerformanceReportStatusLabel } from '@shared/utils';

import { FacilityCalculationPresentation } from './facility-calculation.types';
import {
  toPrimaryBuyoutSummaryData,
  toPrimarySurplusSummaryData,
  toSecondaryBuyoutAfterBuyoutSummaryData,
  toSecondaryBuyoutAfterSurplusSummaryData,
  toSecondarySurplusSummaryData,
  toSecondaryTargetMetRefundSummaryData,
  toSecondaryTargetNotMetRefundSummaryData,
} from './facility-calculation-summary-data';
import { HistoricalFacilityDetails } from './facility-details.resolver';

export function toBuyoutSurplusFacilitySummaryData(
  details: HistoricalFacilityDetails,
  calculation: FacilityCalculationPresentation,
): SummaryData {
  const { facility, runId } = details;
  const common = new SummaryFactory()
    .addSection('Details')
    .addRow('Run ID', runId)
    .addRow('Facility ID', facility.facilityBusinessId)
    .addRow('Site name', facility.siteName)
    .addRow(
      'TPR Version',
      `${facility.targetPeriod}-V${facility.version} (${getFacilityPerformanceReportStatusLabel(facility.performanceOutcome)})`,
    )
    .addRow('Type', facility.primaryRulesApplied ? 'Primary' : 'Secondary')
    .create();

  switch (calculation.scenario) {
    case 'primary-surplus':
      return [...common, ...toPrimarySurplusSummaryData(facility)];
    case 'primary-buyout':
      return [...common, ...toPrimaryBuyoutSummaryData(facility, calculation.showTransaction)];
    case 'secondary-surplus':
      return [...common, ...toSecondarySurplusSummaryData(facility)];
    case 'secondary-target-met-refund':
      return [...common, ...toSecondaryTargetMetRefundSummaryData(facility)];
    case 'secondary-buyout-after-buyout':
      return [...common, ...toSecondaryBuyoutAfterBuyoutSummaryData(facility, calculation.grossBuyoutFee)];
    case 'secondary-buyout-after-surplus':
      return [...common, ...toSecondaryBuyoutAfterSurplusSummaryData(facility, calculation.grossBuyoutFee)];
    case 'secondary-target-not-met-refund':
      return [...common, ...toSecondaryTargetNotMetRefundSummaryData(facility, calculation.grossBuyoutFee)];
  }
}

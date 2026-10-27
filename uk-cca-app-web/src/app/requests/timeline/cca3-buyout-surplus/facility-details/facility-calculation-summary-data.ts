import { SummaryData, SummaryFactory } from '@shared/components';
import { formatGbp } from '@shared/utils';
import BigNumber from 'bignumber.js';

import { formatHistoricalNumber } from '../format-historical-number';
import { HistoricalFacility } from '../historical-facilities';

// Financial rows and order follow PRIMARY / SECONDARY details:
// https://trasys.atlassian.net.mcas.ms/wiki/spaces/CCA/pages/630030420/CCA3+Buy-out+Surplus+Account+Timeline+events
export function toPrimarySurplusSummaryData(facility: HistoricalFacility): SummaryData {
  return new SummaryFactory()
    .addSection('Surplus')
    .addRow('Surplus gained (tCO₂e)', formatHistoricalNumber(facility.surplusGained))
    .create();
}

export function toPrimaryBuyoutSummaryData(facility: HistoricalFacility, showTransaction: boolean): SummaryData {
  const summary = new SummaryFactory()
    .addSection('Buy-out')
    .addRow('Amount above target (tCO₂e)', formatHistoricalNumber(facility.buyOutRequired))
    .addRow('Surplus used (tCO₂e)', formatHistoricalNumber(facility.surplusUsed))
    .addRow('Cost (GBP / tCO₂e)', formatGbp(facility.buyOutCost))
    .addRow('Total buy-out fee (GBP)', formatGbp(facility.invoicedBuyOutFee));
  if (showTransaction) addTransaction(summary, facility);
  return summary.create();
}

export function toSecondarySurplusSummaryData(facility: HistoricalFacility): SummaryData {
  const summary = new SummaryFactory()
    .addSection('Surplus')
    .addRow('Surplus gained (tCO₂e)', formatHistoricalNumber(facility.surplusGained))
    .addRow('Previous surplus gained/removed (tCO₂e)', formatHistoricalNumber(facility.previousSurplusGained));
  if (facility.previousSurplusUsed != null && new BigNumber(facility.previousSurplusUsed).isGreaterThan(0)) {
    summary.addRow('Previous surplus used/re-banked (tCO₂e)', formatHistoricalNumber(facility.previousSurplusUsed));
  }
  return summary.create();
}

export function toSecondaryTargetMetRefundSummaryData(facility: HistoricalFacility): SummaryData {
  const summary = new SummaryFactory()
    .addSection('Refund')
    .addRow('Surplus gained (tCO₂e)', formatHistoricalNumber(facility.surplusGained))
    .addRow('Previous buy-out fee received (GBP)', formatGbp(facility.previousPaidFees))
    .addRow('Previous surplus used/re-banked (tCO₂e)', formatHistoricalNumber(facility.previousSurplusUsed))
    .addRow('Overpayment buy-out amount (GBP)', formatGbp(facility.invoicedBuyOutFee));
  addTransaction(summary, facility);
  return summary.create();
}

export function toSecondaryBuyoutAfterBuyoutSummaryData(
  facility: HistoricalFacility,
  grossBuyoutFee: string | null,
): SummaryData {
  const summary = new SummaryFactory()
    .addSection('Buy-out')
    .addRow('Amount above target (tCO₂e)', formatHistoricalNumber(facility.buyOutRequired))
    .addRow('Cost (GBP / tCO₂e)', formatGbp(facility.buyOutCost))
    .addRow('Total buy-out fee (GBP)', formatGbp(grossBuyoutFee))
    .addRow('Previous buy-out fee received (GBP)', formatGbp(facility.previousPaidFees))
    .addRow('Previous surplus used/re-banked (tCO₂e)', formatHistoricalNumber(facility.previousSurplusUsed))
    .addRow('Secondary buy-out fee (GBP)', formatGbp(facility.invoicedBuyOutFee));
  addPositiveBuyoutTransaction(summary, facility);
  return summary.create();
}

export function toSecondaryBuyoutAfterSurplusSummaryData(
  facility: HistoricalFacility,
  grossBuyoutFee: string | null,
): SummaryData {
  const summary = new SummaryFactory()
    .addSection('Buy-out')
    .addRow('Amount above target (tCO₂e)', formatHistoricalNumber(facility.buyOutRequired))
    .addRow('Cost (GBP / tCO₂e)', formatGbp(facility.buyOutCost))
    .addRow('Total buy-out fee (GBP)', formatGbp(grossBuyoutFee))
    .addRow('Previous surplus gained/removed (tCO₂e)', formatHistoricalNumber(facility.previousSurplusGained));
  if (facility.previousPaidFees != null && new BigNumber(facility.previousPaidFees).isGreaterThan(0)) {
    summary
      .addRow('Previous buy-out fee received (GBP)', formatGbp(facility.previousPaidFees))
      .addRow('Secondary buy-out fee (GBP)', formatGbp(facility.invoicedBuyOutFee));
  }
  addPositiveBuyoutTransaction(summary, facility);
  return summary.create();
}

export function toSecondaryTargetNotMetRefundSummaryData(
  facility: HistoricalFacility,
  grossBuyoutFee: string | null,
): SummaryData {
  const summary = new SummaryFactory()
    .addSection('Refund')
    .addRow('Amount above target (tCO₂e)', formatHistoricalNumber(facility.buyOutRequired))
    .addRow('Cost (GBP / tCO₂e)', formatGbp(facility.buyOutCost))
    .addRow('Total buy-out fee (GBP)', formatGbp(grossBuyoutFee))
    .addRow('Previous buy-out fee received (GBP)', formatGbp(facility.previousPaidFees))
    .addRow('Previous surplus used/re-banked (tCO₂e)', formatHistoricalNumber(facility.previousSurplusUsed))
    .addRow('Overpayment buy-out amount (GBP)', formatGbp(facility.invoicedBuyOutFee));
  addTransaction(summary, facility);
  return summary.create();
}

function addTransaction(summary: SummaryFactory, facility: HistoricalFacility): void {
  if (facility.transactionCode?.trim()) summary.addRow('Transaction ID', facility.transactionCode);
}

function addPositiveBuyoutTransaction(summary: SummaryFactory, facility: HistoricalFacility): void {
  if (
    facility.chargeType === 'FEE' &&
    facility.invoicedBuyOutFee != null &&
    new BigNumber(facility.invoicedBuyOutFee).isGreaterThan(0)
  ) {
    addTransaction(summary, facility);
  }
}

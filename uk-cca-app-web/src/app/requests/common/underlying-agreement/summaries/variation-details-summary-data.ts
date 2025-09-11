import { SummaryData, SummaryFactory } from '@shared/components';

import { UnderlyingAgreementReviewDecision, UnderlyingAgreementVariationDetails } from 'cca-api';

import { VariationChangesEnum } from '../pipes/variation-changes-type.pipe';
import {
  baselineChangesTypes,
  facilityChangesTypes,
  otherChangesTypes,
  targetCurrencyChangesTypes,
} from '../variation-details';
import { addDecisionSummaryData } from './decision-summary-data';

function toSummaryData(
  variationDetails: UnderlyingAgreementVariationDetails,
  isEditable: boolean,
  prefix = '../',
): SummaryFactory {
  const factory = new SummaryFactory().addSection('', prefix + 'variation-details');

  let facilityChanges = [];
  let baselineChanges = [];
  let targetCurrencyChanges = [];
  let otherChanges = [];

  if (variationDetails?.modifications?.length) {
    facilityChanges = facilityChangesTypes.filter((c) => variationDetails.modifications.includes(c));
    baselineChanges = baselineChangesTypes.filter((c) => variationDetails.modifications.includes(c));
    targetCurrencyChanges = targetCurrencyChangesTypes.filter((c) => variationDetails.modifications.includes(c));
    otherChanges = otherChangesTypes.filter((c) => variationDetails.modifications.includes(c));
  }

  if (facilityChanges.length > 0) {
    factory.addTextAreaRow(
      'Target Unit/Facility changes',
      facilityChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  if (baselineChanges.length > 0) {
    factory.addTextAreaRow(
      'Amend the baseline and target due to',
      baselineChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  if (targetCurrencyChanges.length > 0) {
    factory.addTextAreaRow(
      'Amend the target currency to',
      targetCurrencyChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  if (otherChanges.length > 0) {
    factory.addTextAreaRow(
      'Other',
      otherChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  factory.addTextAreaRow('Explain what you are changing and the reason for the changes', variationDetails?.reason, {
    change: isEditable,
    appendChangeParam: true,
  });

  return factory;
}

export function toVariationDetailsSummaryData(
  variationDetails: UnderlyingAgreementVariationDetails,
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  return toSummaryData(variationDetails, isEditable, prefix).create();
}

export function toVariationDetailsSummaryDataWithDecision(
  variationDetails: UnderlyingAgreementVariationDetails,
  isEditable: boolean,
  downloadUrl: string,
  decision: UnderlyingAgreementReviewDecision,
  reviewAttachments: Record<string, string>,
  prefix = '../',
): SummaryData {
  const factory = toSummaryData(variationDetails, isEditable, prefix);
  return addDecisionSummaryData(factory, decision, reviewAttachments, isEditable, downloadUrl).create();
}

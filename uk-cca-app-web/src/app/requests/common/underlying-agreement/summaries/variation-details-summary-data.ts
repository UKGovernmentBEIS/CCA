import { SummaryData, SummaryFactory } from '@shared/components';

import { UnderlyingAgreementReviewDecision, UnderlyingAgreementVariationDetails } from 'cca-api';

import { baselineChangesTypes, facilityChangesTypes, otherChangesTypes, targetCurrencyChangesTypes } from '../modules';
import { VariationChangesEnum } from '../pipes/variation-changes-type.pipe';
import { VariationDetailsWizardStep } from '../underlying-agreement.types';
import { addDecisionSummaryData } from './decision-summary-data';

function toSummaryData(
  variationDetails: UnderlyingAgreementVariationDetails,
  isEditable: boolean,
  prefix = '../',
): SummaryFactory {
  const factory = new SummaryFactory().addSection('', prefix + VariationDetailsWizardStep.DETAILS);

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
    factory.addRow(
      'Target Unit/Facility changes',
      facilityChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
        prewrap: true,
      },
    );
  }

  if (baselineChanges.length > 0) {
    factory.addRow(
      'Amend the baseline and target due to',
      baselineChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
        prewrap: true,
      },
    );
  }

  if (targetCurrencyChanges.length > 0) {
    factory.addRow(
      'Amend the target currency to',
      targetCurrencyChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
        prewrap: true,
      },
    );
  }

  if (otherChanges.length > 0) {
    factory.addRow(
      'Other',
      otherChanges.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
        prewrap: true,
      },
    );
  }

  factory.addRow('Explain what you are changing and the reason for the changes', variationDetails?.reason, {
    change: isEditable,
    appendChangeParam: true,
    prewrap: true,
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

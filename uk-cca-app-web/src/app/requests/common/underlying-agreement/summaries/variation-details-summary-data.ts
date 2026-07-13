import { SummaryData, SummaryFactory } from '@shared/components';

import { UnderlyingAgreementReviewDecision, UnderlyingAgreementVariationDetails } from 'cca-api';

import { VariationChangesEnum } from '../pipes/variation-changes-type.pipe';
import {
  deprecatedChangesTypes,
  dontRequireOperatorAssentTypes,
  otherChangesTypes,
  requireOperatorAssentTypes,
} from '../variation-details';
import { addDecisionSummaryData } from './decision-summary-data';

type ToVariationDetailsSummaryDataArgs = {
  variationDetails: UnderlyingAgreementVariationDetails;
  isEditable: boolean;
  prefix?: string;
};

type ToVariationDetailsSummaryDataWithDecisionArgs = {
  variationDetails: UnderlyingAgreementVariationDetails;
  isEditable: boolean;
  downloadUrl: string;
  decision: UnderlyingAgreementReviewDecision;
  reviewAttachments: Record<string, string>;
  prefix?: string;
};

function toSummaryData(
  variationDetails: UnderlyingAgreementVariationDetails,
  isEditable: boolean,
  prefix = '../',
): SummaryFactory {
  const factory = new SummaryFactory().addSection('', `${prefix}variation-details`);

  let requireOperatorAssent: UnderlyingAgreementVariationDetails['modifications'] = [];
  let dontRequireOperatorAssent: UnderlyingAgreementVariationDetails['modifications'] = [];
  let deprecatedChanges: UnderlyingAgreementVariationDetails['modifications'] = [];
  let otherChanges: UnderlyingAgreementVariationDetails['modifications'] = [];

  if (variationDetails?.modifications?.length) {
    requireOperatorAssent = requireOperatorAssentTypes.filter((c) => variationDetails.modifications.includes(c));
    dontRequireOperatorAssent = dontRequireOperatorAssentTypes.filter((c) =>
      variationDetails.modifications.includes(c),
    );
    deprecatedChanges = deprecatedChangesTypes.filter((c) => variationDetails.modifications.includes(c));
    otherChanges = otherChangesTypes.filter((c) => variationDetails.modifications.includes(c));
  }

  if (requireOperatorAssent.length > 0) {
    factory.addTextAreaRow(
      'Changes that usually require the operator to provide their assent',
      requireOperatorAssent.map((c) => VariationChangesEnum[c]),
      {
        change: isEditable,
        appendChangeParam: true,
      },
    );
  }

  if (dontRequireOperatorAssent.length > 0 || deprecatedChanges.length > 0) {
    factory.addTextAreaRow(
      "Changes that don't usually require the operator to provide their assent",
      [
        ...dontRequireOperatorAssent.map((c) => VariationChangesEnum[c]),
        ...deprecatedChanges.map((c) => VariationChangesEnum[c]),
      ],
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

  factory.addTextAreaRow(
    'Explain in more detail what you are changing and the reason for the changes',
    variationDetails?.reason,
    {
      change: isEditable,
      appendChangeParam: true,
    },
  );

  return factory;
}

export function toVariationDetailsSummaryData(args: ToVariationDetailsSummaryDataArgs): SummaryData {
  return toSummaryData(args.variationDetails, args.isEditable, args.prefix ?? '../').create();
}

export function toVariationDetailsSummaryDataWithDecision(
  args: ToVariationDetailsSummaryDataWithDecisionArgs,
): SummaryData {
  const factory = toSummaryData(args.variationDetails, args.isEditable, args.prefix ?? '../');
  if (!args.decision?.type) return factory.create();
  return addDecisionSummaryData({
    factory,
    decision: args.decision,
    attachments: args.reviewAttachments,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
  }).create();
}

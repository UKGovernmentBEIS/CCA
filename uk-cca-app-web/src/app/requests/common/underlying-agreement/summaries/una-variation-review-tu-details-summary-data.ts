import { SummaryData, SummaryFactory } from '@shared/components';
import { OperatorTypePipe, transformAddress } from '@shared/pipes';
import { Country } from '@shared/types';

import { UnderlyingAgreementReviewDecision, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { ReviewTargetUnitDetailsWizardStep } from '../types';
import { addDecisionSummaryData } from './decision-summary-data';

function toVariationReviewTargetUnitDetailsSummaryFactory(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  countries: Country[],
  isEditable: boolean,
  prefix = '../',
): SummaryFactory {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep?.TARGET_UNIT_DETAILS)
    .addRow('Operator name', targetUnitDetails?.operatorName, {
      change: isEditable,
    })
    .addRow('Operator type', operatorTypePipe.transform(targetUnitDetails?.operatorType), {
      change: isEditable,
    })
    .addRow('Company number', targetUnitDetails?.companyRegistrationNumber ?? 'Not provided', {
      change: isEditable,
      changeLink: prefix + ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
    })
    .addRow('Reason for not having a registration number', targetUnitDetails?.registrationNumberMissingReason, {
      change: isEditable,
      changeLink: prefix + ReviewTargetUnitDetailsWizardStep.COMPANY_REGISTRATION_NUMBER,
    });

  if (targetUnitDetails?.subsectorAssociationName) {
    factory.addRow('Subsector', targetUnitDetails?.subsectorAssociationName, {
      change: isEditable,
    });
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep?.OPERATOR_ADDRESS)
    .addRow('Address', transformAddress(targetUnitDetails?.operatorAddress, countries), {
      change: isEditable,
    })

    .addSection('Responsible Person', prefix + ReviewTargetUnitDetailsWizardStep?.RESPONSIBLE_PERSON)
    .addRow('First name', targetUnitDetails?.responsiblePersonDetails?.firstName, {
      change: isEditable,
    })
    .addRow('Last name', targetUnitDetails?.responsiblePersonDetails?.lastName, {
      change: isEditable,
    })
    .addRow('Email address', targetUnitDetails?.responsiblePersonDetails?.email, {
      change: isEditable,
    })
    .addRow('Address', transformAddress(targetUnitDetails?.responsiblePersonDetails?.address, countries), {
      change: isEditable,
    });

  return factory;
}

type ToVariationReviewTargetUnitDetailsSummaryDataArgs = {
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails;
  countries: Country[];
  isEditable: boolean;
  prefix?: string;
};

type ToVariationReviewTargetUnitDetailsSummaryDataWithDecisionArgs = {
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails;
  decision: UnderlyingAgreementReviewDecision;
  countries: Country[];
  attachments: Record<string, string>;
  downloadUrl: string;
  isEditable: boolean;
  prefix?: string;
};

export function toVariationReviewTargetUnitDetailsSummaryData(
  args: ToVariationReviewTargetUnitDetailsSummaryDataArgs,
): SummaryData {
  return toVariationReviewTargetUnitDetailsSummaryFactory(
    args.targetUnitDetails,
    args.countries,
    args.isEditable,
    args.prefix ?? '../',
  ).create();
}

export function toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
  args: ToVariationReviewTargetUnitDetailsSummaryDataWithDecisionArgs,
): SummaryData {
  const factory = toVariationReviewTargetUnitDetailsSummaryFactory(
    args.targetUnitDetails,
    args.countries,
    args.isEditable,
    args.prefix ?? '../',
  );
  if (!args.decision?.type) return factory.create();
  return addDecisionSummaryData({
    factory,
    decision: args.decision,
    attachments: args.attachments,
    isEditable: args.isEditable,
    downloadUrl: args.downloadUrl,
  }).create();
}

function toVariationReviewTargetUnitDetailsOriginalSummaryFactory(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  countries: Country[],
  isEditable: boolean,
  prefix = '../',
): SummaryFactory {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep?.TARGET_UNIT_DETAILS)
    .addRow('Operator name', targetUnitDetails.operatorName, {
      change: isEditable,
    })
    .addRow('Operator type', operatorTypePipe.transform(targetUnitDetails.operatorType), {
      change: isEditable,
    })
    .addRow('Company number', targetUnitDetails.companyRegistrationNumber ?? 'Not provided', {
      change: isEditable,
    })
    .addRow('Reason for not having a registration number', targetUnitDetails.registrationNumberMissingReason, {
      change: isEditable,
    });

  if (targetUnitDetails.subsectorAssociationName) {
    factory.addRow('Subsector', targetUnitDetails.subsectorAssociationName, {
      change: isEditable,
    });
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep?.OPERATOR_ADDRESS)
    .addRow('Address', transformAddress(targetUnitDetails.operatorAddress, countries), {
      change: isEditable,
    })
    .addSection('Responsible Person', prefix + ReviewTargetUnitDetailsWizardStep?.RESPONSIBLE_PERSON)
    .addRow('First name', targetUnitDetails.responsiblePersonDetails.firstName, {
      change: isEditable,
    })
    .addRow('Last name', targetUnitDetails.responsiblePersonDetails.lastName, {
      change: isEditable,
    })
    .addRow('Email address', targetUnitDetails.responsiblePersonDetails.email, {
      change: isEditable,
    })
    .addRow('Address', transformAddress(targetUnitDetails.responsiblePersonDetails.address, countries), {
      change: isEditable,
    });

  return factory;
}

type ToVariationReviewTargetUnitDetailsOriginalSummaryDataArgs = {
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails;
  countries: Country[];
  isEditable: boolean;
  prefix?: string;
};

export function toVariationReviewTargetUnitDetailsOriginalSummaryData(
  args: ToVariationReviewTargetUnitDetailsOriginalSummaryDataArgs,
): SummaryData {
  return toVariationReviewTargetUnitDetailsOriginalSummaryFactory(
    args.targetUnitDetails,
    args.countries,
    args.isEditable,
    args.prefix ?? '../',
  ).create();
}

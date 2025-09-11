import { SummaryData, SummaryFactory } from '@shared/components';
import { OperatorTypePipe } from '@shared/pipes';
import { getAddressAsArray } from '@shared/utils';

import { UnderlyingAgreementReviewDecision, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { ReviewTargetUnitDetailsWizardStep } from '../types';
import { addDecisionSummaryData } from './decision-summary-data';

function toVariationReviewTargetUnitDetailsSummaryFactory(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
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
    })
    .addRow('Reason for not having a registration number', targetUnitDetails?.registrationNumberMissingReason, {
      change: isEditable,
    });

  if (targetUnitDetails?.subsectorAssociationName) {
    factory.addRow('Subsector', targetUnitDetails?.subsectorAssociationName, {
      change: isEditable,
    });
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep?.OPERATOR_ADDRESS)
    .addRow('Address', getAddressAsArray(targetUnitDetails?.operatorAddress), {
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
    .addRow('Address', getAddressAsArray(targetUnitDetails?.responsiblePersonDetails?.address), {
      change: isEditable,
    });

  return factory;
}

export function toVariationReviewTargetUnitDetailsSummaryData(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  return toVariationReviewTargetUnitDetailsSummaryFactory(targetUnitDetails, isEditable, prefix).create();
}

export function toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  decision: UnderlyingAgreementReviewDecision,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  const factory = toVariationReviewTargetUnitDetailsSummaryFactory(targetUnitDetails, isEditable, prefix);
  return addDecisionSummaryData(factory, decision, attachments, isEditable, downloadUrl).create();
}

function toVariationReviewTargetUnitDetailsOriginalSummaryFactory(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
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
    .addRow('Address', getAddressAsArray(targetUnitDetails.operatorAddress), {
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
    .addRow('Address', getAddressAsArray(targetUnitDetails.responsiblePersonDetails.address), {
      change: isEditable,
    });

  return factory;
}

export function toVariationReviewTargetUnitDetailsOriginalSummaryData(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  return toVariationReviewTargetUnitDetailsOriginalSummaryFactory(targetUnitDetails, isEditable, prefix).create();
}

export function toVariationReviewTargetUnitDetailsOriginalSummaryDataWithDecision(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  decision: UnderlyingAgreementReviewDecision,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  const factory = toVariationReviewTargetUnitDetailsOriginalSummaryFactory(targetUnitDetails, isEditable, prefix);
  return addDecisionSummaryData(factory, decision, attachments, isEditable, downloadUrl).create();
}

import { SummaryData, SummaryFactory } from '@shared/components/summary';
import { OperatorTypePipe } from '@shared/pipes/operator-type.pipe';
import { getAddressAsArray } from '@shared/utils/address';

import { UnderlyingAgreementReviewDecision, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { ReviewTargetUnitDetailsWizardStep } from '../underlying-agreement.types';
import { addDecisionSummaryData } from './decision-summary-data';

function reviewTargetUnitDetailsSummaryFactor(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  isEditable: boolean,
  prefix: string = '../',
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
    .addRow(
      'Company registration number',
      !targetUnitDetails?.isCompanyRegistrationNumber
        ? targetUnitDetails?.registrationNumberMissingReason
        : targetUnitDetails?.companyRegistrationNumber,
      {
        change: isEditable,
      },
    );

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

export function toReviewTargetUnitDetailsUNAReviewSummaryData(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  isEditable: boolean,
  prefix: string = '../',
): SummaryData {
  return reviewTargetUnitDetailsSummaryFactor(targetUnitDetails, isEditable, prefix).create();
}

export function toReviewTargetUnitDetailsSummaryDataWithDecision(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  decision: UnderlyingAgreementReviewDecision,
  attachments: Record<string, string>,
  downloadUrl: string,
  isEditable: boolean,
  prefix: string = '../',
): SummaryData {
  const factory = reviewTargetUnitDetailsSummaryFactor(targetUnitDetails, isEditable, prefix);
  return addDecisionSummaryData(factory, decision, attachments, isEditable, downloadUrl).create();
}

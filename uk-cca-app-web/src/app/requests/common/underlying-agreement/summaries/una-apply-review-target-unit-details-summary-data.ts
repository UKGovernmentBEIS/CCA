import { SummaryData, SummaryFactory } from '@shared/components/summary';
import { OperatorTypePipe } from '@shared/pipes/operator-type.pipe';
import { getAddressAsArray } from '@shared/utils/address';

import { AccountReferenceData, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { ReviewTargetUnitDetailsWizardStep } from '../underlying-agreement.types';

export function toReviewTargetUnitDetailsSummaryData(
  accountReferenceData: AccountReferenceData,
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  isEditable: boolean,
  prefix: string = '../',
): SummaryData {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep?.TARGET_UNIT_DETAILS)
    .addRow('Operator name', targetUnitDetails?.operatorName, {
      change: isEditable,
    })
    .addRow('Operator type', operatorTypePipe.transform(accountReferenceData.targetUnitAccountDetails.operatorType))
    .addRow('Company registration number', accountReferenceData.targetUnitAccountDetails.companyRegistrationNumber);

  if (accountReferenceData.sectorAssociationDetails.subsectorAssociationName) {
    factory.addRow('Subsector', accountReferenceData.sectorAssociationDetails.subsectorAssociationName);
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

  return factory.create();
}

export function toReviewTargetUnitDetailsOriginalSummaryData(
  accountReferenceData: AccountReferenceData,
  isEditable: boolean,
  prefix: string = '../',
): SummaryData {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep?.TARGET_UNIT_DETAILS)
    .addRow('Operator name', accountReferenceData.targetUnitAccountDetails.operatorName, {
      change: isEditable,
    })
    .addRow('Operator type', operatorTypePipe.transform(accountReferenceData.targetUnitAccountDetails.operatorType))
    .addRow('Company registration number', accountReferenceData.targetUnitAccountDetails.companyRegistrationNumber);

  if (accountReferenceData.sectorAssociationDetails.subsectorAssociationName) {
    factory.addRow('Subsector', accountReferenceData.sectorAssociationDetails.subsectorAssociationName);
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep?.OPERATOR_ADDRESS)
    .addRow('Address', getAddressAsArray(accountReferenceData.targetUnitAccountDetails.address), {
      change: isEditable,
    })
    .addSection('Responsible Person', prefix + ReviewTargetUnitDetailsWizardStep?.RESPONSIBLE_PERSON)
    .addRow('First name', accountReferenceData.targetUnitAccountDetails.responsiblePerson.firstName, {
      change: isEditable,
    })
    .addRow('Last name', accountReferenceData.targetUnitAccountDetails.responsiblePerson.lastName, {
      change: isEditable,
    })
    .addRow('Email address', accountReferenceData.targetUnitAccountDetails.responsiblePerson.email, {
      change: isEditable,
    })
    .addRow('Address', getAddressAsArray(accountReferenceData.targetUnitAccountDetails.responsiblePerson.address), {
      change: isEditable,
    });

  return factory.create();
}

import { SummaryData, SummaryFactory } from '@shared/components';
import { OperatorTypePipe, transformAddress } from '@shared/pipes';
import { Country } from '@shared/types';

import { AccountReferenceData, UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { ReviewTargetUnitDetailsWizardStep } from '../types';

export function toVariationTargetUnitDetailsSummaryData(
  targetUnitDetails: UnderlyingAgreementTargetUnitDetails,
  countries: Country[],
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep?.TARGET_UNIT_DETAILS)
    .addRow('Operator name', targetUnitDetails?.operatorName, {
      change: isEditable,
    })
    .addRow('Operator type', operatorTypePipe.transform(targetUnitDetails?.operatorType))
    .addRow('Company number', targetUnitDetails?.companyRegistrationNumber ?? 'Not provided')
    .addRow('Reason for not having a registration number', targetUnitDetails?.registrationNumberMissingReason);

  if (targetUnitDetails?.subsectorAssociationName) {
    factory.addRow('Subsector', targetUnitDetails?.subsectorAssociationName);
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

  return factory.create();
}

export function toVariationTargetUnitDetailsOriginalSummaryData(
  accountReferenceData: AccountReferenceData,
  countries: Country[],
  isEditable: boolean,
  prefix = '../',
): SummaryData {
  const operatorTypePipe = new OperatorTypePipe();

  const factory = new SummaryFactory()
    .addSection('Target unit details', prefix + ReviewTargetUnitDetailsWizardStep?.TARGET_UNIT_DETAILS)
    .addRow('Operator name', accountReferenceData.targetUnitAccountDetails.operatorName, {
      change: isEditable,
    })
    .addRow('Operator type', operatorTypePipe.transform(accountReferenceData.targetUnitAccountDetails.operatorType))
    .addRow('Company number', accountReferenceData.targetUnitAccountDetails.companyRegistrationNumber ?? 'Not provided')
    .addRow(
      'Reason for not having a registration number',
      accountReferenceData.targetUnitAccountDetails.registrationNumberMissingReason,
    );

  if (accountReferenceData.sectorAssociationDetails.subsectorAssociationName) {
    factory.addRow('Subsector', accountReferenceData.sectorAssociationDetails.subsectorAssociationName);
  }

  factory
    .addSection('Operator address', prefix + ReviewTargetUnitDetailsWizardStep?.OPERATOR_ADDRESS)
    .addRow('Address', transformAddress(accountReferenceData.targetUnitAccountDetails.address, countries), {
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
    .addRow(
      'Address',
      transformAddress(accountReferenceData.targetUnitAccountDetails.responsiblePerson.address, countries),
      {
        change: isEditable,
      },
    );

  return factory.create();
}
